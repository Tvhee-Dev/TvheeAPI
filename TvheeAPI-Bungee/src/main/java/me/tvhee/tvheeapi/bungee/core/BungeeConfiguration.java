package me.tvhee.tvheeapi.bungee.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import me.tvhee.tvheeapi.api.config.CommentLoader;
import me.tvhee.tvheeapi.api.config.ConfigurationSerializable;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.core.ConfigurationSerialization;

public final class BungeeConfiguration implements Configuration
{
	private final CommentLoader commentLoader = new CommentLoader();
	private net.md_5.bungee.config.Configuration config;

	@Override
	public void saveConfig(File configFile) throws IOException
	{
		net.md_5.bungee.config.ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).save(config, configFile);
		commentLoader.apply(configFile);
	}

	@Override
	public void setHeader(List<String> header)
	{
		commentLoader.setHeader(header);
	}

	@Override
	public List<String> getHeader()
	{
		return commentLoader.getHeader();
	}

	@Override
	public List<String> getComments(String key)
	{
		return commentLoader.getComments(key);
	}

	@Override
	public void setComments(String key, List<String> comments)
	{
		if(key == null)
			commentLoader.setComments(key, null);

		commentLoader.setComments(key, comments);
	}

	@Override
	public void addComment(String key, String comment)
	{
		List<String> comments = commentLoader.getComments(key);
		comments.add(comment);
		commentLoader.setComments(key, comments);
	}

	@Override
	public void loadConfig(File configFile) throws IOException
	{
		commentLoader.load(configFile);
		config = net.md_5.bungee.config.ConfigurationProvider.getProvider(net.md_5.bungee.config.YamlConfiguration.class).load(configFile);
	}

	@Override
	public boolean contains(String path)
	{
		return config.contains(path);
	}

	@Override
	public void set(String path, Object value)
	{
		config.set(path, value);
	}

	@Override
	public void set(String path, ConfigurationSerializable configurationSerializable)
	{
		set(path, ConfigurationSerialization.serialize(configurationSerializable));
	}

	@Override
	public Collection<String> getKeys()
	{
		return config.getKeys();
	}

	@Override
	public List<Byte> getByteList(String path)
	{
		return config.getByteList(path);
	}

	@Override
	public List<Short> getShortList(String path)
	{
		return config.getShortList(path);
	}

	@Override
	public int getInt(String path)
	{
		return config.getInt(path);
	}

	@Override
	public List<Integer> getIntList(String path)
	{
		return config.getIntList(path);
	}

	@Override
	public long getLong(String path)
	{
		return config.getLong(path);
	}

	@Override
	public List<Long> getLongList(String path)
	{
		return config.getLongList(path);
	}

	@Override
	public List<Float> getFloatList(String path)
	{
		return config.getFloatList(path);
	}

	@Override
	public double getDouble(String path)
	{
		return config.getDouble(path);
	}

	@Override
	public List<Double> getDoubleList(String path)
	{
		return config.getDoubleList(path);
	}

	@Override
	public boolean getBoolean(String path)
	{
		return config.getBoolean(path);
	}

	@Override
	public List<Boolean> getBooleanList(String path)
	{
		return config.getBooleanList(path);
	}

	@Override
	public List<Character> getCharList(String path)
	{
		return config.getCharList(path);
	}

	@Override
	public String getString(String path)
	{
		return config.getString(path);
	}

	@Override
	public List<String> getStringList(String path)
	{
		return config.getStringList(path);
	}

	@Override
	public Collection<String> getKeys(boolean deep)
	{
		return config.getKeys();
	}

	@Override
	public boolean isSet(String path)
	{
		return get(path) != null;
	}

	@Override
	public Object get(String path)
	{
		Object object = config.get(path);

		if(object instanceof Map)
		{
			Map<String, Object> valuesMap = (Map<String, Object>) object;
			if(valuesMap.containsKey("=="))
				return ConfigurationSerialization.deserialize(valuesMap);
		}

		return config.get(path);
	}

	@Override
	public String getString(String path, String def)
	{
		return config.getString(path, def);
	}

	@Override
	public boolean isString(String path)
	{
		return config.get(path) instanceof String;
	}

	@Override
	public int getInt(String path, int def)
	{
		return config.getInt(path, def);
	}

	@Override
	public boolean isInt(String path)
	{
		return config.get(path) instanceof Integer;
	}

	@Override
	public boolean getBoolean(String path, boolean def)
	{
		return config.getBoolean(path, def);
	}

	@Override
	public boolean isBoolean(String path)
	{
		return config.get(path) instanceof Boolean;
	}

	@Override
	public double getDouble(String path, double def)
	{
		return config.getDouble(path, def);
	}

	@Override
	public boolean isDouble(String path)
	{
		return config.get(path) instanceof Double;
	}

	@Override
	public long getLong(String path, long def)
	{
		return config.getLong(path, def);
	}

	@Override
	public boolean isLong(String path)
	{
		return config.get(path) instanceof Long;
	}

	@Override
	public List<?> getList(String path)
	{
		return config.getList(path);
	}

	@Override
	public List<?> getList(String path, List<?> def)
	{
		return config.getList(path, def);
	}

	@Override
	public boolean isList(String path)
	{
		return config.get(path) instanceof List;
	}

	@Override
	public List<Integer> getIntegerList(String path)
	{
		return config.getIntList(path);
	}

	@Override
	public List<Character> getCharacterList(String path)
	{
		return config.getCharList(path);
	}

	@Override
	public List<Map<?, ?>> getMapList(String path)
	{
		List<?> list = getList(path);
		List<Map<?, ?>> result = new ArrayList<>();

		if(list == null)
			return result;

		for(Object object : list)
		{
			if(object instanceof Map)
				result.add((Map<?, ?>) object);
		}

		return result;
	}

	@Override
	public <T> T getObject(String path, Class<T> clazz)
	{
		Object def = get(path);
		return getObject(path, clazz, clazz.isInstance(def) ? clazz.cast(def) : null);
	}

	@Override
	public <T> T getObject(String path, Class<T> clazz, T def)
	{
		Object val = get(path, def);
		return clazz.isInstance(val) ? clazz.cast(val) : def;
	}

	public <T> Object get(String path, T def)
	{
		return config.get(path, def);
	}

	@Override
	public byte getByte(String path)
	{
		return config.getByte(path);
	}

	@Override
	public byte getByte(String path, byte def)
	{
		return config.getByte(path, def);
	}

	@Override
	public short getShort(String path)
	{
		return config.getShort(path);
	}

	@Override
	public short getShort(String path, short def)
	{
		return config.getShort(path, def);
	}

	@Override
	public float getFloat(String path)
	{
		return config.getFloat(path);
	}

	@Override
	public float getFloat(String path, float def)
	{
		return config.getFloat(path, def);
	}

	@Override
	public char getChar(String path)
	{
		return config.getChar(path);
	}

	@Override
	public char getChar(String path, char def)
	{
		return config.getChar(path, def);
	}

	@Override
	public Object getOriginal()
	{
		return this.config;
	}
}
