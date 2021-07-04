package me.tvhee.tvheeapi.spigot.core;

import java.util.ArrayList;
import java.util.List;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.command.CommandSender;
import me.tvhee.tvheeapi.api.scheduler.SchedulerTime;
import org.bukkit.permissions.PermissionAttachment;

public class BukkitCommandSender implements CommandSender
{
	private final BukkitPluginLoader bukkitPluginLoader;
	private final org.bukkit.command.CommandSender bukkitSender;
	private final List<PermissionAttachment> attachments = new ArrayList<>();

	public BukkitCommandSender(BukkitPluginLoader bukkitPluginLoader, org.bukkit.command.CommandSender bukkitSender)
	{
		this.bukkitPluginLoader = bukkitPluginLoader;
		this.bukkitSender = bukkitSender;
	}

	@Override
	public void sendMessage(Component Component)
	{
		bukkitSender.spigot().sendMessage(Component.toChatComponent(MessageType.CHAT_LINE));
	}

	@Override
	public void sendMessage(Component... components)
	{
		for(me.tvhee.tvheeapi.api.chat.Component Component : components)
			this.sendMessage(Component);
	}

	@Override
	public void sendMessage(List<Component> components)
	{
		this.sendMessage(components.toArray(components.toArray(new Component[0])));
	}

	@Override
	public boolean hasPermission(String permission)
	{
		return this.bukkitSender.hasPermission(permission);
	}

	@Override
	public boolean isConsole()
	{
		return this.bukkitSender instanceof org.bukkit.command.ConsoleCommandSender;
	}

	@Override
	public boolean isOp()
	{
		return this.bukkitSender.isOp();
	}

	@Override
	public void setOp(boolean op)
	{
		this.bukkitSender.setOp(op);
	}

	@Override
	public void addPermission(String permission)
	{
		this.attachments.add(this.bukkitSender.addAttachment(bukkitPluginLoader, permission, true));
	}

	@Override
	public void addPermission(String permission, long time, SchedulerTime schedulerTime)
	{
		addPermission(permission);
		TvheeAPI.getInstance().getScheduler().schedule(() -> removePermission(permission), time, schedulerTime);
	}

	@Override
	public void removePermission(String permission)
	{
		for(PermissionAttachment permissionAttachment : this.attachments)
			permissionAttachment.unsetPermission(permission);
	}

	@Override
	public void removePermission(String permission, long time, SchedulerTime schedulerTime)
	{
		removePermission(permission);
		TvheeAPI.getInstance().getScheduler().schedule(() -> addPermission(permission), time, schedulerTime);
	}

	@Override
	public Object getOriginal()
	{
		return this.bukkitSender;
	}

	@Override
	public boolean isBukkit()
	{
		return true;
	}

	@Override
	public boolean isBungee()
	{
		return false;
	}
}
