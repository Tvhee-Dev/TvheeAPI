package me.tvhee.tvheeapi.core;

import java.util.List;
import java.util.UUID;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.file.CustomFile;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;

public final class TvheeAPICore extends TvheeAPI
{
	private final TvheeAPIModule module;
	private Component prefix = new Component();

	public TvheeAPICore(TvheeAPIModule module)
	{
		this.module = module;
	}

	@Override
	public Player getPlayer(String name)
	{
		return module.getPlayer(name);
	}

	@Override
	public Player getPlayer(UUID uuid)
	{
		return module.getPlayer(uuid);
	}

	@Override
	public ConsoleCommandSender getConsole()
	{
		return module.getConsoleSender();
	}

	@Override
	public boolean isBukkit()
	{
		return module.isBukkit();
	}

	@Override
	public boolean isBungee()
	{
		return module.isBungee();
	}

	@Override
	public PluginManager getPluginManager()
	{
		return module.getPluginManager();
	}

	@Override
	public Configuration getConfiguration()
	{
		return module.getConfiguration();
	}

	@Override
	public void setPrefix(Component prefix)
	{
		this.prefix = prefix;
	}

	@Override
	public Component getPrefix()
	{
		return prefix;
	}

	@Override
	public List<Player> getOnlinePlayers()
	{
		return module.getOnlinePlayers();
	}

	@Override
	public Scheduler getScheduler()
	{
		return module.getScheduler();
	}

	public void updatePluginFile(CustomFile newFile)
	{
		module.updatePluginFile(newFile);
	}
}
