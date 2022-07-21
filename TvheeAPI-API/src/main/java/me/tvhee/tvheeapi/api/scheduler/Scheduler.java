package me.tvhee.tvheeapi.api.scheduler;

public interface Scheduler
{
	void cancel(int taskId);

	void cancel(ScheduledTask scheduledTask);

	void cancelAll();

	ScheduledTask runAsync(Runnable runnable);

	ScheduledTask run(Runnable runnable);

	ScheduledTask schedule(Runnable runnable, long delay, SchedulerTime schedulerTime);

	ScheduledTask schedule(Runnable runnable, long delay, long period, SchedulerTime schedulerTime);

	Object getOriginal();

	boolean isBukkit();

	boolean isBungee();
}
