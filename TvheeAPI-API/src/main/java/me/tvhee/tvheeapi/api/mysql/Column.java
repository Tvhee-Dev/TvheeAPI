package me.tvhee.tvheeapi.api.mysql;

public final class Column
{
	private final String name;
	private final String dataType;
	private final boolean primary;

	public Column(String name, String dataType)
	{
		this(name, dataType, false);
	}

	public Column(String name, String dataType, boolean primary)
	{
		this.name = name;
		this.dataType = dataType;
		this.primary = primary;
	}

	public String getName()
	{
		return name;
	}

	public String getDataType()
	{
		return dataType;
	}

	public boolean isPrimary()
	{
		return primary;
	}
}
