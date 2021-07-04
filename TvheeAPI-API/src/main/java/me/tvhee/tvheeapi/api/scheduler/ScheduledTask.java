package me.tvhee.tvheeapi.api.scheduler;

public interface ScheduledTask
{
	int getId();

	Object getOwner();

	void cancel();

	boolean isCancelled();

	Object getOriginal();

	boolean isBukkit();

	boolean isBungee();
}
