package me.tvhee.tvheeapi.api.file;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.config.Configuration;

public class ConfigFile extends ResourceFile
{
	private final Configuration configuration;
	private Locale locale;

	public ConfigFile(ConfigFile configFile)
	{
		super(configFile);
		this.configuration = configFile.configuration;
		this.locale = configFile.locale;
	}

	public ConfigFile(String filePath, String resourcePath)
	{
		super(filePath, resourcePath);
		this.configuration = TvheeAPI.getInstance().getConfiguration();
	}

	public ConfigFile(String parent, String child, String resourcePath)
	{
		super(parent, child, resourcePath);
		this.configuration = TvheeAPI.getInstance().getConfiguration();
	}

	public ConfigFile(File parent, String child, String resourcePath)
	{
		super(parent, child, resourcePath);
		this.configuration = TvheeAPI.getInstance().getConfiguration();
	}

	public ConfigFile(URI uri, String resourcePath)
	{
		super(uri, resourcePath);
		this.configuration = TvheeAPI.getInstance().getConfiguration();
	}

	public ConfigFile(File file, String resourcePath)
	{
		super(file, resourcePath);
		this.configuration = TvheeAPI.getInstance().getConfiguration();
	}

	public ConfigFile(CustomFile file, String resourcePath)
	{
		super(file, resourcePath);
		this.configuration = TvheeAPI.getInstance().getConfiguration();
	}

	public Configuration getConfig()
	{
		if(configuration == null)
			throw new IllegalArgumentException("Configuration was not loaded! Please call saveDefaultConfig() or reloadConfig()!");

		return configuration;
	}

	public Locale getLocale()
	{
		if(locale == null)
			locale = new Locale(getConfig());

		return locale;
	}

	public final void saveDefaultConfig()
	{
		this.saveResourceContent();
		this.reloadConfig();
	}

	public final void reloadConfig()
	{
		try
		{
			configuration.loadConfig(this);
			onLoad();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public final void saveConfig()
	{
		try
		{
			if(configuration == null)
				throw new IllegalArgumentException("Configuration is never loaded!");

			onSave();
			configuration.saveConfig(this);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void onLoad() {}

	protected void onSave() {}

	@Override
	protected final List<String> getSupportedTypes()
	{
		return Arrays.asList("yml", "yaml");
	}
}
