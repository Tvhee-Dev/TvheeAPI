package me.tvhee.tvheeapi.api.command;

public interface ConsoleCommandSender extends CommandSender
{
	default boolean isConsole()
	{
		return true;
	}
}
