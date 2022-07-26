package me.tvhee.tvheeapi.api.description;

import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;

public enum ApiVersion
{
	v1_13("1.13"), v1_14("1.14"), v1_15("1.15"), v1_16("1.16"), v1_17("1.17"), v1_18("1.18"), v1_19("1.19");

	private final String value;

	ApiVersion(String value)
	{
		this.value = value;
	}

	public static ApiVersion fromString(String apiVersion)
	{
		for(ApiVersion version : ApiVersion.values())
		{
			if(version.value.equals(apiVersion))
				return version;
		}

		throw new TvheeAPIInternalException(ApiVersion.class, "fromString", "ApiVersion not found!");
	}

	@Override
	public String toString()
	{
		return value;
	}
}
