package me.tvhee.tvheeapi.spigot.core;

import me.tvhee.tvheeapi.api.scheduler.ScheduledTask;

public final class BukkitTask implements ScheduledTask
{
	private final org.bukkit.scheduler.BukkitTask bukkitTask;
	private boolean cancelled;

	public BukkitTask(org.bukkit.scheduler.BukkitTask bukkitTask)
	{
		this.bukkitTask = bukkitTask;
	}

	@Override
	public int getId()
	{
		return bukkitTask.getTaskId();
	}

	@Override
	public Object getOwner()
	{
		return bukkitTask.getOwner();
	}

	@Override
	public void cancel()
	{
		bukkitTask.cancel();
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
		return bukkitTask;
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
