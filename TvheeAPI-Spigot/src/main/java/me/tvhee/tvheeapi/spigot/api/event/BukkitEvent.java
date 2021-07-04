package me.tvhee.tvheeapi.spigot.api.event;

import me.tvhee.tvheeapi.api.event.Event;
import org.bukkit.event.HandlerList;

public abstract class BukkitEvent extends org.bukkit.event.Event implements Event
{
	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	@Override
	public final HandlerList getHandlers()
	{
		return handlers;
	}
}
