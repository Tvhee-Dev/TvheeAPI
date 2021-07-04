package me.tvhee.tvheeapi.api.exception;

public final class TvheeAPIInternalException extends RuntimeException implements DebugException
{
	private final String clazz;
	private final String method;
	private final String message;

	public TvheeAPIInternalException(Class<?> classInstance, String methodName, Throwable cause)
	{
		this(classInstance.toString(), methodName, cause);
	}

	public TvheeAPIInternalException(Class<?> classInstance, String methodName, String message)
	{
		this(classInstance.toString(), methodName, new Throwable(message));
	}

	public TvheeAPIInternalException(String classInstance, String methodName, String message)
	{
		this(classInstance, methodName, new Throwable(message));
	}

	public TvheeAPIInternalException(String classInstance, String methodName, Throwable cause)
	{
		super("Something went wrong in method " + methodName + " (" + classInstance + "): " + (cause.getMessage() == null ? "" : cause.getMessage() + (cause.getMessage().endsWith(" ") ? "Please report this exception to the developer of TvheeAPI!" : " Please report this exception to the developer of TvheeAPI!")), cause);

		this.clazz = classInstance;
		this.method = methodName;
		this.message = cause.getMessage() == null ? "" : cause.getMessage();

		DebugMessage.exception(this);
	}

	public String getClazz()
	{
		return clazz;
	}

	public String getMethod()
	{
		return method;
	}

	public String getCauseMessage()
	{
		return message;
	}
}
