package me.tvhee.tvheeapi.api;

import java.util.List;
import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;

public abstract class TvheeAPI
{
	private static boolean instanceCreated = false;
	private static TvheeAPI instance;

	public static TvheeAPI getInstance()
	{
		return instance;
	}

	public static void setInstance(TvheeAPI instance)
	{
		if(instanceCreated)
			throw new TvheeAPIException(TvheeAPI.class, "setInstance", "Instance is already set!");

		TvheeAPI.instance = instance;
		instanceCreated = true;
	}

	public abstract Player getPlayer(String name);

	public abstract Player getPlayer(UUID uuid);

	public abstract List<Player> getOnlinePlayers();

	public abstract ConsoleCommandSender getConsole();

	public abstract boolean isBukkit();

	public abstract boolean isBungee();

	public abstract PluginManager getPluginManager();

	public abstract Configuration getConfiguration();

	public abstract Scheduler getScheduler();

	public abstract void setPrefix(Component prefix);

	public abstract Component getPrefix();
}
