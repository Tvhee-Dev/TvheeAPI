package me.tvhee.tvheeapi.bungee.api.event;

import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.annotations.Register;
import me.tvhee.tvheeapi.api.annotations.RegistrationType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.player.Player;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

@Register(RegistrationType.BUNGEE_LISTENER)
public class BungeeListener implements Listener
{
	public final TvheeAPI api = TvheeAPI.getInstance();

	public Player getPlayer(ProxiedPlayer bungeePlayer)
	{
		return api.getPlayer(bungeePlayer.getUniqueId());
	}

	public void sendMessage(ProxiedPlayer bungeePlayer, Component... text)
	{
		getPlayer(bungeePlayer).sendMessage(text);
	}

	public void sendMessage(ProxiedPlayer bungeePlayer, String... text)
	{
		for(String textLine : text)
			getPlayer(bungeePlayer).sendMessage(new Component(textLine));
	}
}
