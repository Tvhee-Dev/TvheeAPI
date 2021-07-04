package me.tvhee.tvheeapi.api.config;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface Configuration
{
	void setHeader(List<String> header);

	List<String> getHeader();

	List<String> getComments(String key);

	void setComments(String key, List<String> comments);

	void addComment(String key, String comment);

	void loadConfig(File configFile) throws Exception;

	void saveConfig(File configFile) throws Exception;

	boolean contains(String path);

	void set(String path, Object value);

	void set(String path, ConfigurationSerializable configurationSerializable);

	Collection<String> getKeys();

	List<Byte> getByteList(String path);

	List<Short> getShortList(String path);

	int getInt(String path);

	List<Integer> getIntList(String path);

	long getLong(String path);

	List<Long> getLongList(String path);

	List<Float> getFloatList(String path);

	double getDouble(String path);

	List<Double> getDoubleList(String path);

	boolean getBoolean(String path);

	List<Boolean> getBooleanList(String path);

	List<Character> getCharList(String path);

	String getString(String path);

	List<String> getStringList(String path);

	/**
	 * Get the keys of the configuration
	 *
	 * @param deep Only available at Bukkit
	 * @return The keys as Collection
	 */

	Collection<String> getKeys(boolean deep);

	boolean isSet(String path);

	Object get(String path);

	String getString(String path, String def);

	boolean isString(String path);

	int getInt(String path, int def);

	boolean isInt(String path);

	boolean getBoolean(String path, boolean def);

	boolean isBoolean(String path);

	double getDouble(String path, double def);

	boolean isDouble(String path);

	long getLong(String path, long def);

	boolean isLong(String path);

	List<?> getList(String path);

	List<?> getList(String path, List<?> def);

	boolean isList(String path);

	List<Integer> getIntegerList(String path);

	List<Character> getCharacterList(String path);

	List<Map<?, ?>> getMapList(String path);

	<T> T getObject(String path, Class<T> clazz);

	<T> T getObject(String path, Class<T> clazz, T def);

	byte getByte(String path);

	byte getByte(String path, byte def);

	short getShort(String path);

	short getShort(String path, short def);

	float getFloat(String path);

	float getFloat(String path, float def);

	char getChar(String path);

	char getChar(String path, char def);

	Object getOriginal();
}
