package me.tvhee.tvheeapi.api.command;

import java.util.ArrayList;
import java.util.List;
import me.tvhee.tvheeapi.api.chat.Component;

public class CommandInformation
{
	private final String name;
	private String permission;
	private Component noPermissionMessage;
	private List<String> aliases = new ArrayList<>();

	public CommandInformation(String name)
	{
		this.name = name.contains("/") ? name.replaceAll("/", "") : name;
	}

	public CommandInformation(String name, String permission, Component noPermissionMessage)
	{
		this.name = name.contains("/") ? name.replaceAll("/", "") : name;
		this.permission = permission;
		this.noPermissionMessage = noPermissionMessage;
	}

	public CommandInformation(String name, String permission, Component noPermissionMessage, List<String> aliases)
	{
		this.name = name.contains("/") ? name.replaceAll("/", "") : name;
		this.permission = permission;
		this.noPermissionMessage = noPermissionMessage;
		this.aliases = aliases;
	}

	public final String getPermission()
	{
		return this.permission;
	}

	public final void setPermission(String permission)
	{
		this.permission = permission;
	}

	public final Component getNoPermissionMessage()
	{
		return this.noPermissionMessage;
	}

	public final void setNoPermissionMessage(Component noPermissionMessage)
	{
		this.noPermissionMessage = noPermissionMessage;
	}

	public final List<String> getAliases()
	{
		return this.aliases;
	}

	public final void setAliases(List<String> aliases)
	{
		this.aliases = aliases;
	}

	public final String getName()
	{
		return this.name;
	}
}
