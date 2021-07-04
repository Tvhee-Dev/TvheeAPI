package me.tvhee.tvheeapi.api.description;

import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;

public enum PluginLoader
{
	BUKKIT_PLUGIN("me.tvhee.tvheeapi.spigot.core.BukkitPluginLoader"), BUNGEE_PLUGIN("me.tvhee.tvheeapi.bungee.core.BungeePluginLoader");

	private final String value;

	PluginLoader(String value)
	{
		this.value = value;
	}

	public static PluginLoader fromString(String pluginLoader)
	{
		return switch(pluginLoader)
				{
					case "me.tvhee.tvheeapi.spigot.core.BukkitPluginLoader" -> PluginLoader.BUKKIT_PLUGIN;
					case "me.tvhee.tvheeapi.bungee.core.BungeePluginLoader" -> PluginLoader.BUNGEE_PLUGIN;
					default -> throw new TvheeAPIInternalException(PluginLoader.class, "fromString", "PluginLoader not found!");
				};
	}

	@Override
	public String toString()
	{
		return value;
	}
}
