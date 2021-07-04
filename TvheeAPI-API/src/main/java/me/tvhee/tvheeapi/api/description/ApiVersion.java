package me.tvhee.tvheeapi.api.description;

import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;

public enum ApiVersion
{
	v1_13("1.13", false), v1_14("1.14", false), v1_15("1.15", false), v1_16("1.16", false), v1_17("1.17", true);

	private final String value;
	private final boolean supported;

	ApiVersion(String value, boolean supported)
	{
		this.value = value;
		this.supported = supported;
	}

	public static ApiVersion fromString(String apiVersion)
	{
		return switch(apiVersion)
				{
					case "1.13" -> v1_13;
					case "1.14" -> v1_14;
					case "1.15" -> v1_15;
					case "1.16" -> v1_16;
					case "1.17" -> v1_17;
					default -> throw new TvheeAPIInternalException(ApiVersion.class, "fromString", "ApiVersion not found!");
				};
	}

	public boolean isSupported()
	{
		return supported;
	}

	@Override
	public String toString()
	{
		return value;
	}
}
