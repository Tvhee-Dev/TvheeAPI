package me.tvhee.tvheeapi.api.description;

public enum PermissionDefault
{
	TRUE("true"), FALSE("false"), OP("op"), NOT_OP("not-op");

	private final String value;

	PermissionDefault(String value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return value;
	}
}
