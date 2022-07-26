package me.tvhee.tvheeapi.core;

import java.util.logging.Logger;

public final class TvheeAPILogger extends Logger
{
	public TvheeAPILogger(Logger parent)
	{
		super("TvheeAPI", null);
		setParent(parent);
	}
}
