package me.tvhee.tvheeapi.api.locale;

import java.io.File;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.files.YamlConfigFile;

public final class Language
{
	public final TvheeAPI api = TvheeAPI.getInstance();
	private final String languageCode;
	private final YamlConfigFile config;

	public Language(File languageDirectory, String configName, String languageCode)
	{
		this.config = api.getFile(new File(languageDirectory, "/" + configName)).getAsYaml();
		this.languageCode = languageCode;
	}

	public YamlConfigFile getFile()
	{
		return config;
	}

	public String getLanguageCode()
	{
		return languageCode;
	}
}
