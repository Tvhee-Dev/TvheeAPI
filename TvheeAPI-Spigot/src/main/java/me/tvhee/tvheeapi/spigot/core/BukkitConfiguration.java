package me.tvhee.tvheeapi.spigot.core;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.tvhee.tvheeapi.api.config.CommentLoader;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.config.ConfigurationSerializable;
import me.tvhee.tvheeapi.api.reflection.Reflection;
import me.tvhee.tvheeapi.core.ConfigurationSerialization;
import org.bukkit.configuration.file.YamlConfiguration;

public final class BukkitConfiguration implements Configuration
{
	private final org.bukkit.configuration.file.YamlConfiguration bukkitConfiguration = new YamlConfiguration();
	private final CommentLoader commentLoader = new CommentLoader();

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
	public void loadConfig(File configFile) throws Exception
	{
		commentLoader.load(configFile);
		bukkitConfiguration.load(configFile);
	}

	@Override
	public void saveConfig(File configFile) throws Exception
	{
		bukkitConfiguration.save(configFile);
		commentLoader.apply(configFile);
	}

	@Override
	public boolean contains(String path)
	{
		return bukkitConfiguration.contains(path);
	}

	@Override
	public Object get(String path)
	{
		Object object = bukkitConfiguration.get(path);

		if(object instanceof Map)
		{
			Map<String, Object> valuesMap = (Map<String, Object>) object;
			if(valuesMap.containsKey("=="))
				return ConfigurationSerialization.deserialize(valuesMap);
		}

		return bukkitConfiguration.get(path);
	}

	@Override
	public String getString(String path, String def)
	{
		return bukkitConfiguration.getString(path, def);
	}

	@Override
	public boolean isString(String path)
	{
		return bukkitConfiguration.isString(path);
	}

	@Override
	public int getInt(String path, int def)
	{
		return bukkitConfiguration.getInt(path, def);
	}

	@Override
	public boolean isInt(String path)
	{
		return bukkitConfiguration.isInt(path);
	}

	@Override
	public boolean getBoolean(String path, boolean def)
	{
		return bukkitConfiguration.getBoolean(path, def);
	}

	@Override
	public boolean isBoolean(String path)
	{
		return bukkitConfiguration.isBoolean(path);
	}

	@Override
	public double getDouble(String path, double def)
	{
		return bukkitConfiguration.getDouble(path, def);
	}

	@Override
	public boolean isDouble(String path)
	{
		return bukkitConfiguration.isDouble(path);
	}

	@Override
	public long getLong(String path, long def)
	{
		return bukkitConfiguration.getLong(path, def);
	}

	@Override
	public boolean isLong(String path)
	{
		return bukkitConfiguration.isLong(path);
	}

	@Override
	public List<?> getList(String path)
	{
		return bukkitConfiguration.getList(path);
	}

	@Override
	public List<?> getList(String path, List<?> def)
	{
		return bukkitConfiguration.getList(path, def);
	}

	@Override
	public boolean isList(String path)
	{
		return bukkitConfiguration.isList(path);
	}

	@Override
	public List<Integer> getIntegerList(String path)
	{
		return bukkitConfiguration.getIntegerList(path);
	}

	@Override
	public List<Character> getCharacterList(String path)
	{
		return bukkitConfiguration.getCharacterList(path);
	}

	@Override
	public List<Map<?, ?>> getMapList(String path)
	{
		return bukkitConfiguration.getMapList(path);
	}

	@Override
	public <T> T getObject(String path, Class<T> clazz)
	{
		return bukkitConfiguration.getObject(path, clazz);
	}

	@Override
	public <T> T getObject(String path, Class<T> clazz, T def)
	{
		return bukkitConfiguration.getObject(path, clazz, def);
	}

	@Override
	public byte getByte(String path)
	{
		Object def = new Reflection(bukkitConfiguration).invokeMethod("getDefault", path).getObject();
		return getByte(path, def instanceof Number ? ((Number) def).byteValue() : 0);
	}

	@Override
	public byte getByte(String path, byte def)
	{
		Object val = bukkitConfiguration.get(path, def);
		return val instanceof Number ? ((Number) val).byteValue() : def;
	}

	@Override
	public short getShort(String path)
	{
		Object def = new Reflection(bukkitConfiguration).invokeMethod("getDefault", path).getObject();
		return getShort(path, def instanceof Number ? ((Number) def).shortValue() : 0);
	}

	@Override
	public short getShort(String path, short def)
	{
		Object val = bukkitConfiguration.get(path, def);
		return val instanceof Number ? ((Number) val).shortValue() : def;
	}

	@Override
	public float getFloat(String path)
	{
		Object def = new Reflection(bukkitConfiguration).invokeMethod("getDefault", path).getObject();
		return getFloat(path, def instanceof Number ? ((Number) def).floatValue() : 0.0F);
	}

	@Override
	public float getFloat(String path, float def)
	{
		Object val = bukkitConfiguration.get(path, def);
		return val instanceof Number ? ((Number) val).floatValue() : def;
	}

	@Override
	public char getChar(String path)
	{
		Object def = new Reflection(bukkitConfiguration).invokeMethod("getDefault", path).getObject();
		return getChar(path, def instanceof Character ? (Character) def : '\u0000');
	}

	@Override
	public char getChar(String path, char def)
	{
		Object val = bukkitConfiguration.get(path, def);
		return val instanceof Character ? (Character) val : def;
	}

	@Override
	public Object getOriginal()
	{
		return this.bukkitConfiguration;
	}

	@Override
	public void set(String path, Object value)
	{
		if(value == null)
			commentLoader.setComments(path, null);

		bukkitConfiguration.set(path, value);
	}

	@Override
	public void set(String path, ConfigurationSerializable configurationSerializable)
	{
		set(path, ConfigurationSerialization.serialize(configurationSerializable));
	}

	@Override
	public Collection<String> getKeys()
	{
		return bukkitConfiguration.getKeys(false);
	}

	@Override
	public List<Byte> getByteList(String path)
	{
		return bukkitConfiguration.getByteList(path);
	}

	@Override
	public List<Short> getShortList(String path)
	{
		return bukkitConfiguration.getShortList(path);
	}

	@Override
	public int getInt(String path)
	{
		return bukkitConfiguration.getInt(path);
	}

	@Override
	public List<Integer> getIntList(String path)
	{
		return bukkitConfiguration.getIntegerList(path);
	}

	@Override
	public long getLong(String path)
	{
		return bukkitConfiguration.getLong(path);
	}

	@Override
	public List<Long> getLongList(String path)
	{
		return bukkitConfiguration.getLongList(path);
	}

	@Override
	public List<Float> getFloatList(String path)
	{
		return bukkitConfiguration.getFloatList(path);
	}

	@Override
	public double getDouble(String path)
	{
		return bukkitConfiguration.getDouble(path);
	}

	@Override
	public List<Double> getDoubleList(String path)
	{
		return bukkitConfiguration.getDoubleList(path);
	}

	@Override
	public boolean getBoolean(String path)
	{
		return bukkitConfiguration.getBoolean(path);
	}

	@Override
	public List<Boolean> getBooleanList(String path)
	{
		return bukkitConfiguration.getBooleanList(path);
	}

	@Override
	public List<Character> getCharList(String path)
	{
		return bukkitConfiguration.getCharacterList(path);
	}

	@Override
	public String getString(String path)
	{
		return bukkitConfiguration.getString(path);
	}

	@Override
	public List<String> getStringList(String path)
	{
		return bukkitConfiguration.getStringList(path);
	}

	@Override
	public Set<String> getKeys(boolean deep)
	{
		return bukkitConfiguration.getKeys(deep);
	}

	@Override
	public boolean isSet(String path)
	{
		return bukkitConfiguration.isSet(path);
	}
}
