package me.tvhee.tvheeapi.bungee.core;

import java.util.List;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.command.CommandSender;
import me.tvhee.tvheeapi.api.scheduler.SchedulerTime;

public class BungeeCommandSender implements CommandSender
{
	private final net.md_5.bungee.api.CommandSender bungeeSender;
	private boolean op;

	public BungeeCommandSender(net.md_5.bungee.api.CommandSender bungeeSender)
	{
		this.bungeeSender = bungeeSender;
	}

	@Override
	public void sendMessage(Component component)
	{
		bungeeSender.sendMessage(component.toChatComponent(MessageType.CHAT_LINE));
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
		this.sendMessage(components.toArray(new Component[0]));
	}

	@Override
	public boolean hasPermission(String permission)
	{
		return this.bungeeSender.hasPermission(permission);
	}

	@Override
	public boolean isConsole()
	{
		return this.bungeeSender.equals(net.md_5.bungee.api.ProxyServer.getInstance().getConsole());
	}

	@Override
	public boolean isOp()
	{
		return op;
	}

	@Override
	public void setOp(boolean op)
	{
		this.op = op;
	}

	@Override
	public void addPermission(String permission)
	{
		this.bungeeSender.setPermission(permission, true);
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
		this.bungeeSender.setPermission(permission, false);
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
		return this.bungeeSender;
	}

	@Override
	public boolean isBukkit()
	{
		return false;
	}

	@Override
	public boolean isBungee()
	{
		return true;
	}
}
