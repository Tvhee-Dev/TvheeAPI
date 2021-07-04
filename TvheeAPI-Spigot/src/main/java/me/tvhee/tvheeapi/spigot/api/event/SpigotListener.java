package me.tvhee.tvheeapi.spigot.api.event;

import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.annotations.Register;
import me.tvhee.tvheeapi.api.annotations.RegistrationType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.player.Player;
import org.bukkit.event.Listener;

@Register(RegistrationType.SPIGOT_LISTENER)
public class SpigotListener implements Listener
{
	public final TvheeAPI api = TvheeAPI.getInstance();

	public Player getPlayer(org.bukkit.entity.Player bukkitPlayer)
	{
		return api.getPlayer(bukkitPlayer.getUniqueId());
	}

	public void sendMessage(org.bukkit.entity.Player bukkitPlayer, Component... text)
	{
		getPlayer(bukkitPlayer).sendMessage(text);
	}

	public void sendMessage(org.bukkit.entity.Player bukkitPlayer, String... text)
	{
		for(String textLine : text)
			getPlayer(bukkitPlayer).sendMessage(new Component(textLine));
	}
}
