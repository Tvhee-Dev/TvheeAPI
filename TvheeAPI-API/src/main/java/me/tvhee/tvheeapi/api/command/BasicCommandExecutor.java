package me.tvhee.tvheeapi.api.command;

import java.util.List;
import me.tvhee.tvheeapi.api.annotations.Register;
import me.tvhee.tvheeapi.api.annotations.RegistrationType;
import me.tvhee.tvheeapi.api.chat.Component;

@Register(RegistrationType.COMMAND)
public abstract class BasicCommandExecutor extends CommandInformation implements CommandExecutor
{
	public BasicCommandExecutor(String name)
	{
		super(name);
	}

	public BasicCommandExecutor(String name, String permission, Component noPermissionMessage)
	{
		super(name, permission, noPermissionMessage);
	}

	public BasicCommandExecutor(String name, String permission, Component noPermissionMessage, List<String> aliases)
	{
		super(name, permission, noPermissionMessage, aliases);
	}

	@Override
	public final CommandInformation getCommandInformation()
	{
		return this;
	}
}
