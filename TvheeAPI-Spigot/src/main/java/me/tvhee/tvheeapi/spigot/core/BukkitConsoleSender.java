package me.tvhee.tvheeapi.spigot.core;

import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;

public final class BukkitConsoleSender extends BukkitCommandSender implements ConsoleCommandSender
{
	public BukkitConsoleSender(BukkitPluginLoader bukkitPluginLoader, org.bukkit.command.ConsoleCommandSender bukkitSender)
	{
		super(bukkitPluginLoader, bukkitSender);
	}

	@Override
	public boolean isConsole()
	{
		return true;
	}
}
