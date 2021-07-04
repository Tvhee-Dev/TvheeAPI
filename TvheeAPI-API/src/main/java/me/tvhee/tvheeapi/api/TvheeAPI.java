package me.tvhee.tvheeapi.api;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.files.CustomFile;
import me.tvhee.tvheeapi.api.files.YamlConfigFile;
import me.tvhee.tvheeapi.api.locale.LocaleManager;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;
import me.tvhee.tvheeapi.api.updater.SpigotUpdateChecker.UpdateState;

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

	public abstract CustomFile getFile(String path);

	public abstract CustomFile getFile(File file);

	public abstract Player getPlayer(String name);

	public abstract Player getPlayer(UUID uuid);

	public abstract ConsoleCommandSender getConsole();

	public abstract LocaleManager getLanguageManager(File directory);

	public abstract boolean isBukkit();

	public abstract boolean isBungee();

	public abstract PluginManager getPluginManager();

	public abstract InputStream getResource(String name);

	public abstract void updateConfig(InputStream newConfig, YamlConfigFile currentConfig);

	public abstract UpdateState checkForUpdates();

	public abstract void setPrefix(Component prefix);

	public abstract Component getPrefix();

	public abstract Collection<Player> getOnlinePlayers();

	public abstract Scheduler getScheduler();
}
