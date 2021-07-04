package me.tvhee.tvheeapi.api.config;

import java.util.Map;

public interface ConfigurationSerializable
{
	Map<String, ?> serialize();

	Object deserialize(Map<String, ?> values);
}
