package me.tvhee.tvheeapi.api.file;

public enum LocalePrefix
{
	INFO("&8&l[&9&li&8&l]&7 "),
	SUCCESS("&8&l[&2&l\u2714&8&l]&7 "),
	WARNING("&8&l[&6&l!&8&l]&6 "),
	ERROR("&8&l[&4&l\u2715&8&l]&c "),
	NO_PREFIX("");

	private final String prefix;

	LocalePrefix(String prefix)
	{
		this.prefix = prefix;
	}

	@Override
	public String toString()
	{
		return prefix;
	}
}
