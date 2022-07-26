package me.tvhee.tvheeapi.api.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.annotations.PluginMain;
import me.tvhee.tvheeapi.api.annotations.Require;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.description.PluginDescription;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.file.ConfigFile;
import me.tvhee.tvheeapi.core.TvheeAPIModule;

@Require(PluginMain.class)
public abstract class TvheeAPIPlugin
{
	public final TvheeAPI api;
	private static TvheeAPIPlugin instance;
	private TvheeAPIModule universalPluginLoader;

	private boolean enabled = false;
	private boolean init = false;
	private Logger logger;
	private PluginDescription description;
	private File dataFolder;
	private ConfigFile config;

	protected TvheeAPIPlugin()
	{
		this.api = TvheeAPI.getInstance();
	}

	public static TvheeAPIPlugin getInstance()
	{
		return instance;
	}

	public static Object getPlugin()
	{
		return instance.universalPluginLoader;
	}

	public final void init(TvheeAPIModule plugin, Logger logger, PluginDescription description, File dataFolder)
	{
		if(init)
			throw new TvheeAPIException(getClass(), "init", "Please do not init the plugin again!");
		else
			init = true;

		this.universalPluginLoader = plugin;
		this.logger = logger;
		this.description = description;
		this.dataFolder = dataFolder;
		instance = this;
	}

	public final void onLoad()
	{
		try
		{
			this.config = new ConfigFile(getDataFolder(), "config.yml");
		}
		catch(IllegalArgumentException e)
		{
			this.config = null;
		}

		onPluginLoad();
	}

	public abstract void onPluginLoad();

	public final void onEnable()
	{
		onPluginEnable();
	}

	public abstract void onPluginEnable();

	public final void onDisable()
	{
		onPluginDisable();
	}

	public abstract void onPluginDisable();

	public final Configuration getConfig()
	{
		checkConfigNotNull();
		return config.getConfig();
	}

	public final void reloadConfig()
	{
		checkConfigNotNull();
		config.reloadConfig();
	}

	public final void saveConfig()
	{
		checkConfigNotNull();
		config.saveConfig();
	}

	public final void saveDefaultConfig()
	{
		checkConfigNotNull();
		config.saveDefaultConfig();
	}

	public final Logger getLogger()
	{
		return logger;
	}

	public final File getDataFolder()
	{
		return dataFolder;
	}

	public final PluginDescription getDescription()
	{
		return description;
	}

	public final InputStream getResource(String name)
	{
		return universalPluginLoader.getResource(name);
	}

	public final boolean isBukkit()
	{
		return universalPluginLoader.isBukkit();
	}

	public final boolean isBungee()
	{
		return universalPluginLoader.isBungee();
	}

	public final boolean isEnabled()
	{
		return enabled;
	}

	public final void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
	}

	public final TvheeAPI getApi()
	{
		return TvheeAPI.getInstance();
	}

	private void checkConfigNotNull()
	{
		if(config == null)
			throw new IllegalArgumentException("If you would like to use the config.yml file please include it in your resources folder!");
	}
}
