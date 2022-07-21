package me.tvhee.tvheeapi.api.description;

import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;

public enum PluginLoader
{
	BUKKIT_PLUGIN("me.tvhee.tvheeapi.spigot.core.BukkitPluginLoader"),
	BUNGEE_PLUGIN("me.tvhee.tvheeapi.bungee.core.BungeePluginLoader");

	private final String value;

	PluginLoader(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value;
	}

	public static PluginLoader fromString(String pluginLoader)
	{
		switch(pluginLoader)
		{
			case "me.tvhee.tvheeapi.spigot.core.BukkitPluginLoader":
			{
				return PluginLoader.BUKKIT_PLUGIN;
			}
			case "me.tvhee.tvheeapi.bungee.core.BungeePluginLoader":
			{
				return PluginLoader.BUNGEE_PLUGIN;
			}
			default:
			{
				throw new TvheeAPIInternalException(PluginLoader.class, "fromString", "PluginLoader not" + " found!");
			}
		}
	}
}
