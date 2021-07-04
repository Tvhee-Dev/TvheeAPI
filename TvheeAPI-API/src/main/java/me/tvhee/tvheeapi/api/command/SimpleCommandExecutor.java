package me.tvhee.tvheeapi.api.command;

import java.util.List;
import me.tvhee.tvheeapi.api.annotations.Register;
import me.tvhee.tvheeapi.api.annotations.RegistrationType;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.player.Player;

@Register(RegistrationType.COMMAND)
public abstract class SimpleCommandExecutor extends BasicCommandExecutor
{
	public SimpleCommandExecutor(String command)
	{
		super(command);
	}

	public SimpleCommandExecutor(String name, String permission, Component noPermissionMessage)
	{
		super(name, permission, noPermissionMessage);
	}

	public SimpleCommandExecutor(String name, String permission, Component noPermissionMessage, List<String> aliases)
	{
		super(name, permission, noPermissionMessage, aliases);
	}

	public final void sendMessage(CommandSender sender, Component component)
	{
		if(sender != null)
			sender.sendMessage(component);
	}

	public final void sendMessage(CommandSender sender, Component... component)
	{
		if(sender != null)
			sender.sendMessage(component);
	}

	public final void sendMessage(CommandSender sender, List<Component> component)
	{
		if(sender != null)
			sender.sendMessage(component);
	}

	public final boolean checkPermission(CommandSender sender)
	{
		if(sender == null)
			return false;

		return checkPermission(sender, getPermission());
	}

	public final boolean checkPermission(CommandSender sender, String permission)
	{
		if(sender == null)
			return false;

		if(!sender.hasPermission(permission))
		{
			sender.sendMessage(getNoPermissionMessage());
			return false;
		}

		return true;
	}

	public final boolean checkPlayer(CommandSender sender)
	{
		if(sender == null)
			return false;

		return sender instanceof Player;
	}
}
