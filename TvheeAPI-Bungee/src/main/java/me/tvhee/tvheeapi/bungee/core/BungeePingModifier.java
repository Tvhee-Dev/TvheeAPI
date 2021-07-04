package me.tvhee.tvheeapi.bungee.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.api.serverping.Hover;
import me.tvhee.tvheeapi.api.serverping.Motd;
import me.tvhee.tvheeapi.api.serverping.ServerPing;
import me.tvhee.tvheeapi.core.TvheeAPICore;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public final class BungeePingModifier implements Listener
{
	@EventHandler
	public void onProxyPing(ProxyPingEvent e)
	{
		net.md_5.bungee.api.ServerPing bungeePing = e.getResponse();

		ServerPing defaultServerPing = new ServerPing()
		{
			@Override
			public Motd getMotd()
			{
				return Motd.fromLegacy(bungeePing.getDescriptionComponent().toLegacyText());
			}

			@Override
			public int getOnlinePlayers()
			{
				return bungeePing.getPlayers().getOnline();
			}

			@Override
			public int getMaxPlayers()
			{
				return bungeePing.getPlayers().getMax();
			}

			@Override
			public Hover getHover()
			{
				Map<Component, UUID> hover = new HashMap<>();

				for(int i = 0; i < bungeePing.getPlayers().getSample().length; i++)
					hover.put(new Component(bungeePing.getPlayers().getSample()[i].getName()), bungeePing.getPlayers().getSample()[i].getUniqueId());

				return new Hover(hover);
			}

			@Override
			public Component getProtocolName()
			{
				return new Component(bungeePing.getVersion().getName());
			}

			@Override
			public int getProtocolVersion()
			{
				return bungeePing.getVersion().getProtocol();
			}

			@Override
			public BufferedImage getFavicon()
			{
				try
				{
					return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(bungeePing.getFaviconObject().getEncoded().replaceAll("data:image/png;base64,", ""))));
				}
				catch(IOException ex)
				{
					throw new TvheeAPIInternalException(getClass(), "getFavicon (ImageIO.read())", ex);
				}
			}
		};

		ServerPing serverPing = ((TvheeAPICore) TvheeAPI.getInstance()).getDefaultPing(defaultServerPing);

		if(serverPing.getMotd() != null)
			bungeePing.setDescriptionComponent(new TextComponent(serverPing.getMotd().toLegacy()));

		List<PlayerInfo> playerInfos = new ArrayList<>();

		net.md_5.bungee.api.ServerPing.PlayerInfo[] players = new net.md_5.bungee.api.ServerPing.PlayerInfo[serverPing.getHover().getHoverLines() == null ? 0 : serverPing.getHover().getHoverLines().size()];

		if(serverPing.getHover() != null)
		{
			for(Map.Entry<Component, UUID> entry : serverPing.getHover().getHoverLines().entrySet())
				playerInfos.add(new net.md_5.bungee.api.ServerPing.PlayerInfo(entry.getKey().toLegacyText(null), entry.getValue()));

			for(int i = 0; i < playerInfos.size(); i++)
				players[i] = playerInfos.get(i);
		}

		bungeePing.setPlayers(new net.md_5.bungee.api.ServerPing.Players(serverPing.getMaxPlayers() == -1 ? ProxyServer.getInstance().getConfigurationAdapter().getListeners().iterator().next().getMaxPlayers() : serverPing.getMaxPlayers(), serverPing.getOnlinePlayers() == -1 ? ProxyServer.getInstance().getOnlineCount() : serverPing.getOnlinePlayers(), players));

		if(serverPing.getProtocolName() != null)
		{
			if(serverPing.getProtocolName().isCentered())
				TvheeAPIPlugin.getInstance().getLogger().warning("Centering the protocolname is in betà as I don't know the amount of pixels!");
		}

		bungeePing.setVersion(new net.md_5.bungee.api.ServerPing.Protocol(serverPing.getProtocolName() == null ? "" : serverPing.getProtocolName().toLegacyText(MessageType.PROTOCOL_LINE), serverPing.getProtocolVersion() == -1 ? 755 : serverPing.getProtocolVersion()));
		bungeePing.setFavicon(Favicon.create(serverPing.getFavicon()));

		e.setResponse(bungeePing);
	}
}
