package me.tvhee.tvheeapi.api.event;

public interface Event
{
	/**
	 * Is the event cancellable? Then implement the interface, and 'return this'
	 * Is the event not cancellable? Then 'return null'
	 * @return If the event is cancellable
	 */

	Cancellable cancellable();

	boolean runAsync();
}
