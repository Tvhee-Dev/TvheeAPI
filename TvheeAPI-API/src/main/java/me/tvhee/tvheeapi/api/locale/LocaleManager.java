package me.tvhee.tvheeapi.api.locale;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;

public final class LocaleManager
{
	private final File directory;
	private List<Language> languageFiles = new ArrayList<>();
	private String currentLanguage;

	public LocaleManager(File directory) throws IllegalArgumentException
	{
		this.directory = directory;
	}

	public void saveDefaultConfigs(Language[] configs)
	{
		for(Language config : configs)
		{
			if(TvheeAPI.getInstance().getResource(config.getFile().getName()) == null)
			{
				TvheeAPIPlugin.getInstance().getLogger().warning("Language " + config.getFile().getName() + " not found!");
				continue;
			}

			config.getFile().saveDefaultConfig();
			languageFiles.add(config);
		}
	}

	public void saveDefaultConfigs(List<Language> configs)
	{
		saveDefaultConfigs(configs.toArray(new Language[0]));
	}

	public void setRandomLanguage()
	{
		setLanguage(languageFiles.get(0).getLanguageCode());
	}

	public String getLanguage()
	{
		return currentLanguage;
	}

	public void setLanguage(String currentLanguage)
	{
		this.currentLanguage = currentLanguage;
		reload();
	}

	public void reload()
	{
		File[] languageConfigs = directory.listFiles(file -> file.getAbsolutePath().endsWith(".yml"));

		if(languageConfigs == null)
			throw new IllegalArgumentException("No language configs found!");

		Language[] languages = new Language[languageConfigs.length];

		for(int i = 0; i < languageConfigs.length; i++)
			languages[i] = new Language(directory, languageConfigs[i].getName(), languageConfigs[i].getName().replaceAll("\\.yml", ""));

		this.languageFiles = Arrays.asList(languages);

		if(languages.length == 0)
			throw new IllegalArgumentException("No language files found!");

		if(currentLanguage == null)
			currentLanguage = languages[0].getLanguageCode();
	}

	public Configuration getConfig()
	{
		Language config = getLanguage(currentLanguage);

		if(!languageExists() || config == null)
			return null;

		return config.getFile().getConfig();
	}

	public Component getMessage(String path)
	{
		if(!languageExists())
			return null;

		Language config = getLanguage(currentLanguage);
		return new Component(config.getFile().getConfig().getString(path));
	}

	public boolean languageExists()
	{
		return getLanguage(currentLanguage) != null;
	}

	public boolean languageExists(String language)
	{
		return getLanguage(language) != null;
	}

	public List<String> getLoadedLanguages()
	{
		List<String> languages = new ArrayList<>();

		for(Language language : this.languageFiles)
			languages.add(language.getLanguageCode());

		return languages;
	}

	public Language getCurrentLanguage()
	{
		if(languageExists() && getLanguage(currentLanguage) != null)
			return getLanguage(currentLanguage);

		return null;
	}

	public Language getLanguage(String languageCode)
	{
		for(Language config : languageFiles)
		{
			if(config.getLanguageCode().equals(languageCode))
				return config;
		}

		return null;
	}
}
