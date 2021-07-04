package me.tvhee.tvheeapi.api.exception;

public final class TvheeAPIException extends RuntimeException implements DebugException
{
	private final String clazz;
	private final String method;
	private final String message;

	public TvheeAPIException(Class<?> classInstance, String methodName, Throwable cause)
	{
		this(classInstance.toString(), methodName, cause);
	}

	public TvheeAPIException(Class<?> classInstance, String methodName, String message)
	{
		this(classInstance.toString(), methodName, new Throwable(message));
	}

	public TvheeAPIException(String classInstance, String methodName, String message)
	{
		this(classInstance, methodName, new Throwable(message));
	}

	public TvheeAPIException(String classInstance, String methodName, Throwable cause)
	{
		super("Something went wrong in method " + methodName + " (" + classInstance + "): " + (cause.getMessage() == null ? "" : cause.getMessage()), cause);

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
