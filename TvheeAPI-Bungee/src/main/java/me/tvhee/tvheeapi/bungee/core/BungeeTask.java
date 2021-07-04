package me.tvhee.tvheeapi.bungee.core;

import me.tvhee.tvheeapi.api.scheduler.ScheduledTask;

public final class BungeeTask implements ScheduledTask
{
	private final net.md_5.bungee.api.scheduler.ScheduledTask bungeeTask;
	private boolean cancelled;

	public BungeeTask(net.md_5.bungee.api.scheduler.ScheduledTask bungeeTask)
	{
		this.bungeeTask = bungeeTask;
	}

	@Override
	public int getId()
	{
		return bungeeTask.getId();
	}

	@Override
	public Object getOwner()
	{
		return bungeeTask.getOwner();
	}

	@Override
	public void cancel()
	{
		bungeeTask.cancel();
		cancelled = true;
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public Object getOriginal()
	{
		return bungeeTask;
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
