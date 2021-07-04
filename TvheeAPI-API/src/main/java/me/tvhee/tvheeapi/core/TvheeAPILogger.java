package me.tvhee.tvheeapi.core;

import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class TvheeAPILogger extends Logger
{
	public TvheeAPILogger(Logger parent)
	{
		super("TvheeAPI", null);
		setParent(parent);
	}

	public void log(LogRecord logRecord)
	{
		String pluginName = "[TvheeAPI] ";
		logRecord.setMessage(pluginName + logRecord.getMessage());
		super.log(logRecord);
	}
}
