package me.tvhee.tvheeapi.core;

import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import me.tvhee.tvheeapi.api.files.YamlConfigFile;
import org.yaml.snakeyaml.Yaml;

public final class ConfigUpdater
{
	private final YamlConfigFile oldConfig;
	private final Map<String, Object> newConfig;

	public ConfigUpdater(YamlConfigFile oldConfig, InputStream newConfig)
	{
		this.oldConfig = oldConfig;
		this.newConfig = new Yaml().load(newConfig);
	}

	public void applyAndSave()
	{
		for(String key : oldConfig.getConfig().getKeys())
		{
			if(!newConfig.containsKey(key))
				oldConfig.getConfig().set(key, null);
		}

		for(Entry<String, Object> entry : newConfig.entrySet())
		{
			String key = entry.getKey();
			Object value = entry.getValue();

			if(!oldConfig.getConfig().contains(key))
				oldConfig.getConfig().set(key, value);
		}

		oldConfig.saveConfig();
	}
}
