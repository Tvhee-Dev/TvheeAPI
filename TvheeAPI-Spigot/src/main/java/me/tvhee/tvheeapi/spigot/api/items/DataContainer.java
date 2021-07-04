package me.tvhee.tvheeapi.spigot.api.items;

import java.util.Set;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class DataContainer
{
	private final Plugin plugin = (Plugin) TvheeAPIPlugin.getPlugin();
	private final PersistentDataContainer persistentDataContainer;

	public DataContainer(PersistentDataContainer persistentDataContainer)
	{
		this.persistentDataContainer = persistentDataContainer;
	}

	public boolean isEmpty()
	{
		return this.persistentDataContainer.isEmpty();
	}

	public void remove(String key)
	{
		this.persistentDataContainer.remove(new NamespacedKey(plugin, key));
	}

	public void remove(NamespacedKey key)
	{
		this.persistentDataContainer.remove(key);
	}

	public Set<NamespacedKey> getKeys()
	{
		return this.persistentDataContainer.getKeys();
	}

	public <T> boolean has(String key, PersistentDataType<T, T> dataType)
	{
		return this.persistentDataContainer.has(new NamespacedKey(plugin, key), dataType);
	}

	public void addString(String key, String value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, value);
	}

	public void addInteger(String key, int value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER, value);
	}

	public void addBoolean(String key, boolean value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.STRING, String.valueOf(value));
	}

	public void addByte(String key, byte value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.BYTE, value);
	}

	public void addShort(String key, short value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.SHORT, value);
	}

	public void addLong(String key, long value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.LONG, value);
	}

	public void addFloat(String key, float value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.FLOAT, value);
	}

	public void addDouble(String key, double value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.DOUBLE, value);
	}

	public void addByteArray(String key, byte[] value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY, value);
	}

	public void addIntegerArray(String key, int[] value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.INTEGER_ARRAY, value);
	}

	public void addLongArray(String key, long[] value)
	{
		this.persistentDataContainer.set(new NamespacedKey(plugin, key), PersistentDataType.LONG_ARRAY, value);
	}

	public long[] getLongArray(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.LONG_ARRAY);
	}

	public int[] getIntegerArray(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.INTEGER_ARRAY);
	}

	public byte[] getByteArray(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.BYTE_ARRAY);
	}

	public double getDouble(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.DOUBLE);
	}

	public float getFloat(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.FLOAT);
	}

	public long getLong(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.LONG);
	}

	public short getShort(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.SHORT);
	}

	public byte getByte(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.BYTE);
	}

	public String getString(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.STRING);
	}

	public int getInteger(String key)
	{
		return this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.INTEGER);
	}

	public boolean getBoolean(String key)
	{
		return Boolean.parseBoolean(this.persistentDataContainer.get(new NamespacedKey(plugin, key), PersistentDataType.STRING));
	}

	public PersistentDataContainer getDataHolder()
	{
		return this.persistentDataContainer;
	}
}
