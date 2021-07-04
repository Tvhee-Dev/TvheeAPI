package me.tvhee.tvheeapi.api.exception;

public interface DebugException
{
	String getClazz();

	String getMethod();

	String getCauseMessage();
}
