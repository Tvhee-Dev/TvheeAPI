package me.tvhee.tvheeapi.spigot.core;

import java.util.concurrent.atomic.AtomicLong;
import me.tvhee.tvheeapi.api.scheduler.ScheduledTask;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;
import me.tvhee.tvheeapi.api.scheduler.SchedulerTime;

public final class BukkitScheduler implements Scheduler
{
	private final BukkitPluginLoader bukkitPluginLoader;
	private final org.bukkit.scheduler.BukkitScheduler bukkitScheduler;

	public BukkitScheduler(BukkitPluginLoader bukkitPluginLoader, org.bukkit.scheduler.BukkitScheduler bukkitScheduler)
	{
		this.bukkitPluginLoader = bukkitPluginLoader;
		this.bukkitScheduler = bukkitScheduler;
	}

	@Override
	public void cancel(int taskId)
	{
		bukkitScheduler.cancelTask(taskId);
	}

	@Override
	public void cancel(ScheduledTask scheduledTask)
	{
		bukkitScheduler.cancelTask(scheduledTask.getId());
	}

	@Override
	public void cancelAll()
	{
		bukkitScheduler.cancelTasks(bukkitPluginLoader);
	}

	@Override
	public ScheduledTask runAsync(Runnable runnable)
	{
		return new BukkitTask(bukkitScheduler.runTaskAsynchronously(bukkitPluginLoader, runnable));
	}

	@Override
	public ScheduledTask run(Runnable runnable)
	{
		return new BukkitTask(bukkitScheduler.runTask(bukkitPluginLoader, runnable));
	}

	@Override
	public ScheduledTask schedule(Runnable runnable, long delay, SchedulerTime schedulerTime)
	{
		return new BukkitTask(bukkitScheduler.runTaskLater(bukkitPluginLoader, runnable, SchedulerTime.calculateTicks(schedulerTime, delay)));
	}

	@Override
	public ScheduledTask schedule(Runnable runnable, long delay, long period, SchedulerTime schedulerTime)
	{
		return new BukkitTask(bukkitScheduler.runTaskTimer(bukkitPluginLoader, runnable, SchedulerTime.calculateTicks(schedulerTime, delay), SchedulerTime.calculateTicks(schedulerTime, period)));
	}

	@Override
	public ScheduledTask schedule(Runnable runnable, long delay, long period, long repeat, SchedulerTime schedulerTime)
	{
		ScheduledTask task = schedule(runnable, delay, period, schedulerTime);
		AtomicLong repeatedTimes = new AtomicLong();
		ScheduledTask repeatingTask = schedule(() ->
		{
			if(task.isCancelled() || repeatedTimes.get() >= repeat)
				cancel(task);

			repeatedTimes.set(repeatedTimes.get() + 1);
		}, delay, period, schedulerTime);

		schedule(() ->
		{
			if(!task.isCancelled())
				cancel(task);

			cancel(repeatingTask);
		}, delay + (period * repeat), schedulerTime);
		return task;
	}

	private void cancelRepeatingTask(ScheduledTask task)
	{
		task.cancel();
	}

	@Override
	public Object getOriginal()
	{
		return bukkitScheduler;
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
