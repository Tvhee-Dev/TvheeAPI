package me.tvhee.tvheeapi.api.collection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;

public final class CollectionUtil
{
	private CollectionUtil() {}

	public static <T> T getNext(T given, List<T> list, boolean forward)
	{
		if(given == null && list.isEmpty())
			return null;

		final T[] array = (T[]) Array.newInstance((given != null ? given : list.get(0)).getClass(), list.size());

		for(int i = 0; i < list.size(); i++)
			Array.set(array, i, list.get(i));

		return getNext(given, array, forward);
	}

	public static <T> T getNext(T given, T[] array, boolean forward)
	{
		if(array.length == 0)
			return null;

		int index = 0;

		for(int i = 0; i < array.length; i++)
		{
			final T element = array[i];

			if(element.equals(given))
			{
				index = i;
				break;
			}
		}

		int nextIndex = index + (forward ? 1 : -1);
		return nextIndex >= array.length ? array[0] : nextIndex < 0 ? array[array.length - 1] : array[nextIndex];
	}

	public static <V> V pickRandom(List<V> list)
	{
		return list.get(new Random().nextInt(list.size()));
	}

	public static <V> V pickRandom(List<V> list, Function<V, Boolean> canObtain)
	{
		if(list.isEmpty())
			return null;

		list = new ArrayList<>(list);

		while(!list.isEmpty())
		{
			V picked = list.remove(new Random().nextInt(list.size()));

			if(picked != null && canObtain.apply(picked))
				return picked;
		}

		return null;
	}

	public static <K, NV, V> Map<K, NV> convertMapValues(Map<K, V> map, Converter<NV, V> valueConverter)
	{
		Map<K, NV> newMap = new HashMap<>();

		for(Map.Entry<K, V> entry : map.entrySet())
			newMap.put(entry.getKey(), valueConverter.convert(entry.getValue()));

		return newMap;
	}

	public static <NK, K, V> Map<NK, V> convertMapKeys(Map<K, V> map, Converter<NK, K> keyConverter)
	{
		Map<NK, V> newMap = new HashMap<>();

		for(Map.Entry<K, V> entry : map.entrySet())
			newMap.put(keyConverter.convert(entry.getKey()), entry.getValue());

		return newMap;
	}

	public static <NK, NV, K, V> Map<NK, NV> convertMap(Map<K, V> map, Converter<NK, K> keyConverter, Converter<NV, V> valueConverter)
	{
		Map<NK, NV> newMap = new HashMap<>();

		for(Map.Entry<K, V> entry : map.entrySet())
			newMap.put(keyConverter.convert(entry.getKey()), valueConverter.convert(entry.getValue()));

		return newMap;
	}

	public static <NV, V> List<NV> convertList(Collection<V> list, Converter<NV, V> converter)
	{
		List<NV> newList = new ArrayList<>();

		try
		{
			for(V value : list)
			{
				NV converted = converter.convert(value);

				if(converted != null)
					newList.add(converted);
			}
		}
		catch(Exception ignored)
		{

		}

		return newList;
	}

	public static <K, V> Set<Map.Entry<V, K>> reverseEntries(Set<Map.Entry<K, V>> entries)
	{
		return new HashSet<>(convertList(entries, (Converter<Map.Entry<V, K>, Map.Entry<K, V>>) value -> new Map.Entry<V, K>()
		{
			@Override
			public V getKey()
			{
				return value.getValue();
			}

			@Override
			public K getValue()
			{
				return value.getKey();
			}

			@Override
			public K setValue(K value)
			{
				return value;
			}
		}));
	}

	public static <V> List<V> randomize(Collection<V> list)
	{
		List<V> randomized = new ArrayList<>(list);
		Collections.shuffle(randomized);
		return randomized;
	}

	public static <K, V> Map<K, V> randomize(Collection<K> keys, Collection<V> values)
	{
		int size = keys.size();
		Map<K, V> randomEntries = new HashMap<>();

		if(keys.size() != values.size())
			size = Math.min(keys.size(), values.size());

		List<K> randomKeys = randomize(keys);
		List<V> randomValues = randomize(values);

		for(int i = 0; i < size; i++)
			randomEntries.put(randomKeys.get(i), randomValues.get(i));

		return randomEntries;
	}

	public static String format(Collection<String> list)
	{
		StringBuilder stringBuilder = new StringBuilder();

		for(String value : list)
			stringBuilder.append(value).append(", ");

		return stringBuilder.substring(0, stringBuilder.length() - 2);
	}

	public static <V> String format(Collection<V> list, Converter<String, V> stringConverter)
	{
		StringBuilder stringBuilder = new StringBuilder();

		for(V value : list)
			stringBuilder.append(stringConverter.convert(value)).append(", ");

		return stringBuilder.substring(0, stringBuilder.length() - 2);
	}

	public static <V> Map<Integer, V> convertArray(V[] array)
	{
		Map<Integer, V> map = new HashMap<>();

		for(int i = 0; i < array.length; i++)
		{
			V value = array[i];

			if(value != null)
				map.put(i, value);
		}

		return map;
	}

	public static <V> V[] convertArray(Map<Integer, V> map, Class<V> arrayClass)
	{
		V[] array = (V[]) Array.newInstance(arrayClass, map.size());

		for(Map.Entry<Integer, V> entry : map.entrySet())
			array[entry.getKey()] = entry.getValue();

		return array;
	}

	public interface Converter<C, T>
	{
		C convert(T value);
	}
}
