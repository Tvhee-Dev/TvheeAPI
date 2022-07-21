package me.tvhee.tvheeapi.api.scheduler;

public enum SchedulerTime
{
	TICKS(1), SECONDS(20), MINUTES(1200), HOURS(72000), DAYS(1692000);

	private final long ticks;

	SchedulerTime(long ticks)
	{
		this.ticks = ticks;
	}

	public long getTicks()
	{
		return ticks;
	}

	public static long calculateTicks(SchedulerTime time, long amount)
	{
		return time.ticks * amount;
	}

	public static long calculateSeconds(SchedulerTime time, long amount)
	{
		return calculateTicks(time, amount) / 20;
	}
}
