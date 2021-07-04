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
import me.tvhee.tvheeapi.api.files.YamlConfigFile;
import me.tvhee.tvheeapi.core.UniversalPluginLoader;

@Require(PluginMain.class)
public abstract class TvheeAPIPlugin
{
	public final TvheeAPI api = TvheeAPI.getInstance();
	private static UniversalPluginLoader universalPluginLoader;
	private static TvheeAPIPlugin instance;
	private boolean enabled = false;
	private boolean init = false;
	private Logger logger;
	private PluginDescription description;
	private File dataFolder;
	private YamlConfigFile config;

	public static TvheeAPIPlugin getInstance()
	{
		return instance;
	}

	public static Object getPlugin()
	{
		return universalPluginLoader == null ? new Object() : universalPluginLoader;
	}

	public final void init(UniversalPluginLoader universalPluginLoader, Logger logger, PluginDescription description, File dataFolder)
	{
		if(init)
			throw new TvheeAPIException(getClass(), "init", "Please do not init the plugin again!");
		else
			init = true;

		TvheeAPIPlugin.universalPluginLoader = universalPluginLoader;
		this.logger = logger;
		this.description = description;
		this.dataFolder = dataFolder;
		instance = this;
	}

	public final void onLoad()
	{
		this.config = api.getFile("config.yml").getAsYaml();

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
		return config.getConfig();
	}

	public final void reloadConfig()
	{
		config.reloadConfig();
	}

	public final void saveConfig()
	{
		config.saveConfig();
	}

	public final void saveDefaultConfig()
	{
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
}
