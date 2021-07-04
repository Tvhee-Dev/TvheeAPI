package me.tvhee.tvheeapi.api.description;

public final class PluginDescription
{
	private final String pluginName;
	private final String version;
	private final ApiVersion apiVersion;
	private final String description;
	private final PluginLoadOrder loadOrder;
	private final String[] authors;
	private final String website;
	private final String pluginMain;
	private final PluginLoader pluginLoader;
	private final String[] dependencies;
	private final String logPrefix;
	private final String[] softDependencies;
	private final String[] loadBefore;
	private final String[] bungeeListeners;
	private final String[] spigotListeners;
	private final String[] registeredCommands;

	public PluginDescription(String pluginName, String version, ApiVersion apiVersion, String description, PluginLoadOrder loadOrder, String[] authors, String website, String pluginMain, PluginLoader pluginLoader, String[] dependencies, String logPrefix, String[] softDependencies, String[] loadBefore, String[] bungeeListeners, String[] spigotListeners, String[] registeredCommands)
	{
		this.pluginName = pluginName;
		this.version = version;
		this.apiVersion = apiVersion;
		this.description = description;
		this.loadOrder = loadOrder;
		this.authors = authors;
		this.website = website;
		this.pluginMain = pluginMain;
		this.pluginLoader = pluginLoader;
		this.dependencies = dependencies;
		this.logPrefix = logPrefix;
		this.softDependencies = softDependencies;
		this.loadBefore = loadBefore;
		this.bungeeListeners = bungeeListeners;
		this.spigotListeners = spigotListeners;
		this.registeredCommands = registeredCommands;
	}

	public String getPluginName()
	{
		return pluginName;
	}

	public String getVersion()
	{
		return version;
	}

	public ApiVersion getApiVersion()
	{
		return apiVersion;
	}

	public String getDescription()
	{
		return description;
	}

	public PluginLoadOrder getLoadOrder()
	{
		return loadOrder;
	}

	public String[] getAuthors()
	{
		return authors;
	}

	public String getWebsite()
	{
		return website;
	}

	public String getPluginMain()
	{
		return pluginMain;
	}

	public PluginLoader getPluginLoader()
	{
		return pluginLoader;
	}

	public String[] getDependencies()
	{
		return dependencies;
	}

	public String getLogPrefix()
	{
		return logPrefix;
	}

	public String[] getSoftDependencies()
	{
		return softDependencies;
	}

	public String[] getLoadBefore()
	{
		return loadBefore;
	}

	public String[] getBungeeListeners()
	{
		return bungeeListeners;
	}

	public String[] getSpigotListeners()
	{
		return spigotListeners;
	}

	public String[] getRegisteredCommands()
	{
		return registeredCommands;
	}
}
