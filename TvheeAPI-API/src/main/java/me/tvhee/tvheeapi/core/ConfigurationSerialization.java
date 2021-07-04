package me.tvhee.tvheeapi.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.tvhee.tvheeapi.api.config.ConfigurationSerializable;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.reflection.Reflection;

public final class ConfigurationSerialization
{
	private static final List<ConfigurationSerializable> configSerializable = new ArrayList<>();

	public static boolean classIsRegistered(Class<? extends ConfigurationSerializable> clazz)
	{
		for(ConfigurationSerializable configurationSerializable : configSerializable)
		{
			if(configurationSerializable.getClass().equals(clazz))
				return true;
		}

		return false;
	}

	public static Object deserialize(Map<String, ?> deserialize)
	{
		try
		{
			Class<?> clazz = Reflection.getClassWithException((String) deserialize.get("=="));
			Class<? extends ConfigurationSerializable> serializeClass = clazz.asSubclass(ConfigurationSerializable.class);

			if(!classIsRegistered(serializeClass))
				throw new TvheeAPIInternalException(serializeClass, "deserialize", "Class is not registered!");

			for(ConfigurationSerializable configurationSerializable : configSerializable)
			{
				if(configurationSerializable.getClass().equals(serializeClass))
					return configurationSerializable.deserialize(deserialize);
			}

			return null;
		}
		catch(ClassCastException e)
		{
			throw new TvheeAPIInternalException(ConfigurationSerialization.class, "deserialize", "Class is not instance of " + ConfigurationSerializable.class.getName() + "!");
		}
	}

	public static Map<String, ?> serialize(ConfigurationSerializable configurationSerializable)
	{
		Class<? extends ConfigurationSerializable> clazz = configurationSerializable.getClass();

		if(!classIsRegistered(clazz))
			configSerializable.add(configurationSerializable);

		Map<String, Object> serialize = new HashMap<>();
		serialize.put("==", clazz.getName());
		serialize.putAll(configurationSerializable.serialize());
		return serialize;
	}
}
