package me.tvhee.tvheeapi.api.description;

public enum PluginLoadOrder
{
	STARTUP("startup"), POSTWORLD("postworld");

	private final String value;

	PluginLoadOrder(String value)
	{
		this.value = value;
	}

	public static PluginLoadOrder fromString(String pluginLoadOrder)
	{
		return PluginLoadOrder.valueOf(pluginLoadOrder.toUpperCase());
	}

	@Override
	public String toString()
	{
		return value;
	}
}
