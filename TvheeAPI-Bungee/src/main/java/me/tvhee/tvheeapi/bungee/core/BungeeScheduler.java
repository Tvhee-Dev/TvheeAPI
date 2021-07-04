package me.tvhee.tvheeapi.bungee.core;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import me.tvhee.tvheeapi.api.scheduler.ScheduledTask;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;
import me.tvhee.tvheeapi.api.scheduler.SchedulerTime;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public final class BungeeScheduler implements Scheduler
{
	private final BungeePluginLoader bungeePluginLoader;
	private final TaskScheduler bungeeScheduler;

	public BungeeScheduler(BungeePluginLoader bungeePluginLoader, TaskScheduler bungeeScheduler)
	{
		this.bungeePluginLoader = bungeePluginLoader;
		this.bungeeScheduler = bungeeScheduler;
	}

	@Override
	public void cancel(int taskId)
	{
		bungeeScheduler.cancel(taskId);
	}

	@Override
	public void cancel(ScheduledTask scheduledTask)
	{
		bungeeScheduler.cancel((net.md_5.bungee.api.scheduler.ScheduledTask) scheduledTask.getOriginal());
	}

	@Override
	public void cancelAll()
	{
		bungeeScheduler.cancel(bungeePluginLoader);
	}

	@Override
	public ScheduledTask runAsync(Runnable runnable)
	{
		return new BungeeTask(bungeeScheduler.runAsync(bungeePluginLoader, runnable));
	}

	@Override
	public ScheduledTask run(Runnable runnable)
	{
		return new BungeeTask(bungeeScheduler.schedule(bungeePluginLoader, runnable, 0, TimeUnit.MILLISECONDS));
	}

	@Override
	public ScheduledTask schedule(Runnable runnable, long delay, SchedulerTime schedulerTime)
	{
		return new BungeeTask(bungeeScheduler.schedule(bungeePluginLoader, runnable, SchedulerTime.calculateSeconds(schedulerTime, delay), TimeUnit.SECONDS));
	}

	@Override
	public ScheduledTask schedule(Runnable runnable, long delay, long period, SchedulerTime schedulerTime)
	{
		return new BungeeTask(bungeeScheduler.schedule(bungeePluginLoader, runnable, SchedulerTime.calculateSeconds(schedulerTime, delay), SchedulerTime.calculateSeconds(schedulerTime, period), TimeUnit.SECONDS));
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

	@Override
	public Object getOriginal()
	{
		return bungeeScheduler;
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
