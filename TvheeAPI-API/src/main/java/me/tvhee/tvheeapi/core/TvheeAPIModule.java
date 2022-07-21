package me.tvhee.tvheeapi.core;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.file.CustomFile;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;

public interface TvheeAPIModule
{
	Configuration getConfiguration();

	Player getPlayer(String name);

	Player getPlayer(UUID uuid);

	PluginManager getPluginManager();

	ConsoleCommandSender getConsoleSender();

	Scheduler getScheduler();

	List<Player> getOnlinePlayers();

	void updatePluginFile(CustomFile newVersion);

	InputStream getDescriptionResource();

	InputStream getResource(String name);

	File getDataFolder();

	Logger getDefaultLogger();

	Logger getServerLogger();

	boolean isBukkit();

	boolean isBungee();
}
