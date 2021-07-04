package me.tvhee.tvheeapi.core;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.UUID;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.files.CustomFile;
import me.tvhee.tvheeapi.api.files.YamlConfigFile;
import me.tvhee.tvheeapi.api.locale.LocaleManager;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;
import me.tvhee.tvheeapi.api.serverping.ServerPing;
import me.tvhee.tvheeapi.api.updater.SpigotUpdateChecker;
import me.tvhee.tvheeapi.api.updater.SpigotUpdateChecker.UpdateState;

public final class TvheeAPICore extends TvheeAPI
{
	private final TvheeAPILoader module;
	private Component prefix = new Component();

	public TvheeAPICore(TvheeAPILoader module)
	{
		this.module = module;
	}

	@Override
	public CustomFile getFile(String path)
	{
		if(path.endsWith(".yml") || path.endsWith(".yaml"))
			return new YamlConfigFile(module.getDataFolder(), path, module.getYamlConfiguration());
		else
			return new CustomFile(module.getDataFolder(), path);
	}

	@Override
	public CustomFile getFile(File file)
	{
		if(file.getPath().endsWith(".yml") || file.getPath().endsWith(".yaml"))
			return new YamlConfigFile(file, module.getYamlConfiguration());
		else
			return new CustomFile(file);
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
	public LocaleManager getLanguageManager(File directory)
	{
		return new LocaleManager(directory);
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
	public InputStream getResource(String name)
	{
		return module.getResource(name);
	}

	@Override
	public void updateConfig(InputStream newConfig, YamlConfigFile currentConfig)
	{
		ConfigUpdater configUpdater = new ConfigUpdater(currentConfig, newConfig);
		configUpdater.applyAndSave();
	}

	@Override
	public UpdateState checkForUpdates()
	{
		SpigotUpdateChecker updateChecker = module.getPluginManager().getUpdateChecker();

		if(updateChecker != null)
			return updateChecker.checkForUpdates();

		return null;
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
	public Collection<Player> getOnlinePlayers()
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

	public ServerPing getDefaultPing(ServerPing current)
	{
		ServerPing defaultPing = module.getPluginManager().getServerPing();
		return defaultPing == null ? current : defaultPing;
	}
}
