package me.tvhee.tvheeapi.api.exception;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import me.tvhee.tvheeapi.api.file.CustomFile;
import me.tvhee.tvheeapi.core.TvheeAPILogger;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;

public final class DebugMessage
{
	private final Throwable exception;
	private final String clazz;
	private final String method;
	private final String cause;
	private final String date;
	private final String hour;

	private DebugMessage(DebugException exception)
	{
		if(!(exception instanceof Throwable))
			throw new IllegalArgumentException("Exception is not instance of " + Throwable.class + "!");

		this.exception = (Throwable) exception;
		this.clazz = exception.getClazz();
		this.method = exception.getMethod();
		this.cause = exception.getCauseMessage();
		this.hour = "[" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + "]";
		this.date = "[" + DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now()) + "]";
	}

	public void log()
	{
		TvheeAPILogger logger = new TvheeAPILogger(TvheeAPIPlugin.getInstance().getLogger());
		logger.info("Logging exception...");

		String path = DebugMessage.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath;

		try
		{
			decodedPath = URLDecoder.decode(path, "UTF-8");
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return;
		}

		CustomFile file = new CustomFile(new File(decodedPath).getParentFile(), "tvheeapi-exception-logger.txt");
		List<String> content = new ArrayList<>();

		if(!file.exists())
		{
			file.createNewFile();
			content.add("##Exception scriptFile created at " + date + " " + hour + " by TvheeAPI. Please check the console to get more information about exceptions");
		}

		content.add(getLogMessage());

		if(this.cause != null && !this.cause.equals(""))
			content.add(getTimeStamp() + "Message: " + this.cause);

		content.addAll(getStackTrace());
		file.setContent(content);
	}

	public String getTimeStamp()
	{
		return this.date + " " + this.hour + " ";
	}

	public String getLogMessage()
	{
		return getTimeStamp() + "Something went wrong in method " + method + " (" + clazz + ")";
	}

	public List<String> getStackTrace()
	{
		List<String> stackTrace = new ArrayList<>();
		Throwable cause = this.exception;

		while(cause != null)
		{
			List<String> lines = new ArrayList<>();

			for(StackTraceElement stackTraceElement : cause.getStackTrace())
			{
				if(!stackTraceElement.getClassName().startsWith("me.tvhee.tvheeapi") && !stackTraceElement.getClassName().startsWith("net.minecraft") && !stackTraceElement.getClassName().startsWith("org.bukkit") && !stackTraceElement.toString().contains("java.base/java.lang.Thread.run(Thread.java:831)"))
					lines.add(getTimeStamp() + "\t- " + stackTraceElement);
			}

			if(!lines.isEmpty())
			{
				if(!(cause instanceof TvheeAPIException))
					stackTrace.add(getTimeStamp() + "Exception: " + cause.getClass().getName());

				stackTrace.add(getTimeStamp() + "Possible Causes: ");
				stackTrace.addAll(lines);
			}

			cause = cause.getCause();
		}

		return stackTrace;
	}

	public Throwable getException()
	{
		return exception;
	}

	public String getClazz()
	{
		return clazz;
	}

	public String getMethod()
	{
		return method;
	}

	public String getCause()
	{
		return cause;
	}

	public String getDate()
	{
		return date;
	}

	public String getHour()
	{
		return hour;
	}

	public static void exception(DebugException exception)
	{
		if(!(exception instanceof Throwable))
			throw new TvheeAPIException(DebugMessage.class, "exception", exception.getClazz() + " is not an instance of " + Throwable.class + "!");

		DebugMessage instance = new DebugMessage(exception);
		instance.log();
	}
}
