package me.tvhee.tvheeapi.api.files;

import java.io.File;
import java.net.URI;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;

public class YamlConfigFile extends CustomFile
{
	private final Configuration configuration;

	public YamlConfigFile(String filePath, Configuration configuration)
	{
		super(filePath.endsWith(".yml") ? filePath : filePath + ".yml");
		this.configuration = configuration;
	}

	public YamlConfigFile(String parent, String child, Configuration configuration)
	{
		super(parent, child.endsWith(".yml") ? child : child + ".yml");
		this.configuration = configuration;
	}

	public YamlConfigFile(File parent, String child, Configuration configuration)
	{
		super(parent, child.endsWith(".yml") ? child : child + ".yml");
		this.configuration = configuration;
	}

	public YamlConfigFile(URI uri, Configuration configuration)
	{
		super(uri);
		this.configuration = configuration;
	}

	public YamlConfigFile(File file, Configuration configuration)
	{
		super(file);
		this.configuration = configuration;
	}

	private void loadConfig()
	{
		try
		{
			configuration.loadConfig(this);
		}
		catch(Exception e)
		{
			throw new TvheeAPIInternalException(getClass(), "loadConfig", e);
		}
	}

	public void saveDefaultConfig()
	{
		saveDefaultFile();
		loadConfig();
	}

	public void saveConfig()
	{
		try
		{
			configuration.saveConfig(this);
		}
		catch(Exception e)
		{
			throw new TvheeAPIInternalException(getClass(), "saveConfig", e);
		}
	}

	public void reloadConfig()
	{
		loadConfig();
	}

	public Configuration getConfig()
	{
		loadConfig();
		return configuration;
	}
}
