package me.tvhee.tvheeapi.bungee.core;

import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import net.md_5.bungee.api.CommandSender;

public final class BungeeConsoleSender extends BungeeCommandSender implements ConsoleCommandSender
{
	public BungeeConsoleSender(CommandSender bungeeSender)
	{
		super(bungeeSender);
	}

	@Override
	public boolean isConsole()
	{
		return true;
	}
}
