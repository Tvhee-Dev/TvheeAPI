package me.tvhee.tvheeapi.api.event;

public interface Cancellable
{
	void setCancelled(boolean cancel);

	boolean isCancelled();
}
