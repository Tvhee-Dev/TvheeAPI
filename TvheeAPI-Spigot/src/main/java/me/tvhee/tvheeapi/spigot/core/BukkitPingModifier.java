package me.tvhee.tvheeapi.spigot.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.GameProfile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.imageio.ImageIO;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.reflection.Reflection;
import me.tvhee.tvheeapi.api.serverping.Hover;
import me.tvhee.tvheeapi.api.serverping.Motd;
import me.tvhee.tvheeapi.api.serverping.ServerPing;
import me.tvhee.tvheeapi.core.TvheeAPICore;
import net.minecraft.network.chat.ChatModifier;
import net.minecraft.network.chat.ChatModifier.ChatModifierSerializer;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatBaseComponent.ChatSerializer;
import net.minecraft.network.protocol.status.PacketStatusOutServerInfo;
import net.minecraft.network.protocol.status.ServerPing.ServerData;
import net.minecraft.network.protocol.status.ServerPing.ServerPingPlayerSample;
import net.minecraft.util.ChatTypeAdapterFactory;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public final class BukkitPingModifier implements Listener
{
	@EventHandler
	public void onServerListPing(ServerListPingEvent e)
	{
		Gson serverPingJson = new GsonBuilder()
				.registerTypeAdapter(net.minecraft.network.protocol.status.ServerPing.ServerData.class, new net.minecraft.network.protocol.status.ServerPing.ServerData.Serializer())
				.registerTypeAdapter(net.minecraft.network.protocol.status.ServerPing.ServerPingPlayerSample.class, new net.minecraft.network.protocol.status.ServerPing.ServerPingPlayerSample.Serializer())
				.registerTypeAdapter(net.minecraft.network.protocol.status.ServerPing.class, this.getSerializer())
				.registerTypeHierarchyAdapter(net.minecraft.network.chat.IChatBaseComponent.class, new IChatBaseComponent.ChatSerializer())
				.registerTypeHierarchyAdapter(ChatModifier.class, new ChatModifierSerializer())
				.registerTypeAdapterFactory(new ChatTypeAdapterFactory()).create();

		Reflection.setStaticField(PacketStatusOutServerInfo.class, "a", serverPingJson);
	}

	private JsonSerializer<net.minecraft.network.protocol.status.ServerPing> getSerializer()
	{
		return (nmsServerPing, type, jsonSerializationContext) ->
		{
			ServerPing defaultServerPing = new ServerPing()
			{
				@Override
				public Motd getMotd()
				{
					return Motd.fromLegacy(nmsServerPing.a().getText());
				}

				@Override
				public int getOnlinePlayers()
				{
					return nmsServerPing.b().b();
				}

				@Override
				public int getMaxPlayers()
				{
					return nmsServerPing.b().a();
				}

				@Override
				public Hover getHover()
				{
					GameProfile[] nmsHover = nmsServerPing.b().c();

					Map<Component, UUID> hover = new HashMap<>();

					for(GameProfile gameProfile : nmsHover)
						hover.put(new Component(gameProfile.getName()), gameProfile.getId());

					return new Hover(hover);
				}

				@Override
				public Component getProtocolName()
				{
					return new Component(nmsServerPing.getServerData().a());
				}

				@Override
				public int getProtocolVersion()
				{
					return nmsServerPing.getServerData().getProtocolVersion();
				}

				@Override
				public BufferedImage getFavicon()
				{
					String faviconString = nmsServerPing.d();

					if(faviconString != null)
					{
						try
						{
							return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(faviconString.replaceAll("data:image/png;base64,", ""))));
						}
						catch(IOException e)
						{
							throw new TvheeAPIInternalException(getClass(), "getSerializer", e);
						}
					}

					return null;
				}
			};

			ServerPing serverPing = ((TvheeAPICore) TvheeAPI.getInstance()).getDefaultPing(defaultServerPing);

			JsonObject serverPingJsonObject = new JsonObject();

			if(serverPing.getMotd() != null)
				serverPingJsonObject.add("description", jsonSerializationContext.serialize(ChatSerializer.a("{\"text\": \"" + serverPing.getMotd().toLegacy() + "\"}")));
			else
				serverPingJsonObject.add("description", jsonSerializationContext.serialize(nmsServerPing.a()));

			List<GameProfile> gameProfileList = new ArrayList<>();
			GameProfile[] profiles = new GameProfile[serverPing.getHover() == null ? 0 : serverPing.getHover().getHoverLines().size()];

			if(serverPing.getHover() != null)
			{
				for(Map.Entry<Component, UUID> entry : serverPing.getHover().getHoverLines().entrySet())
					gameProfileList.add(new GameProfile(entry.getValue(), entry.getKey().toLegacyText(null)));

				for(int i = 0; i < gameProfileList.size(); i++)
					profiles[i] = gameProfileList.get(i);
			}

			ServerPingPlayerSample players = new ServerPingPlayerSample(serverPing.getMaxPlayers() == -1 ? nmsServerPing.b().a() : serverPing.getMaxPlayers(), serverPing.getOnlinePlayers() == -1 ? nmsServerPing.b().b() : serverPing.getOnlinePlayers());

			if(profiles.length != 0)
				players.a(profiles);

			serverPingJsonObject.add("players", jsonSerializationContext.serialize(players));
			serverPingJsonObject.add("version", jsonSerializationContext.serialize(new ServerData(serverPing.getProtocolName() == null ? nmsServerPing.getServerData().a() : serverPing.getProtocolName().toLegacyText(MessageType.PROTOCOL_LINE), serverPing.getProtocolVersion() == -1 ? nmsServerPing.getServerData().getProtocolVersion() : serverPing.getProtocolVersion())));

			if(serverPing.getFavicon() != null)
			{
				final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

				try
				{
					ImageIO.write(serverPing.getFavicon(), "PNG", byteArrayOutputStream);
				}
				catch(IOException e)
				{
					throw new TvheeAPIInternalException(getClass(), "getSerializer", e);
				}

				String encodedFavicon = "data:image/png;base64," + Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
				serverPingJsonObject.addProperty("favicon", encodedFavicon);
			}
			else
				serverPingJsonObject.addProperty("favicon", nmsServerPing.d());

			return serverPingJsonObject;
		};
	}
}
