package me.tvhee.tvheeapi.core;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.description.ApiVersion;
import me.tvhee.tvheeapi.api.description.PluginDescription;
import me.tvhee.tvheeapi.api.description.PluginLoadOrder;
import me.tvhee.tvheeapi.api.description.PluginLoader;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.api.reflection.Reflection;
import org.yaml.snakeyaml.Yaml;

public final class TvheeAPIPluginLoader
{
	private static boolean instanceCreated = false;
	private final TvheeAPILoader module;
	private final PluginDescription description;
	private final TvheeAPIPlugin plugin;
	private final TvheeAPILogger tvheeApiLogger;

	public TvheeAPIPluginLoader(TvheeAPILoader module)
	{
		if(instanceCreated)
			throw new TvheeAPIException(getClass(), "init", "Please do not create instances of PluginLoader(s)!");

		instanceCreated = true;
		this.module = module;
		TvheeAPI.setInstance(new TvheeAPICore(module));
		this.description = loadDescription(module.getDescriptionResource());
		this.plugin = init(description.getPluginMain());
		this.tvheeApiLogger = new TvheeAPILogger(module.getServerLogger());
	}

	public Logger getApiLogger()
	{
		return tvheeApiLogger;
	}

	public PluginDescription getDescription()
	{
		return description;
	}

	public void init()
	{
		plugin.init(module, module.getDefaultLogger(), description, module.getDataFolder());
	}

	public void loadPlugin()
	{
		if(plugin == null)
			throw new TvheeAPIInternalException(getClass(), "loadPlugin", "Please call init() first!");

		if(plugin.isEnabled())
			return;

		try
		{
			plugin.onLoad();
			tvheeApiLogger.info("Loaded " + plugin.getDescription().getPluginName() + " " + plugin.getDescription().getVersion() + "!");
		}
		catch(Throwable e)
		{
			tvheeApiLogger.warning("Can't load plugin " + plugin.getDescription().getPluginName() + " " + plugin.getDescription().getVersion() + "!");
			throw new TvheeAPIException(plugin.getClass(), "onPluginLoad", e);
		}
	}

	public void enablePlugin()
	{
		if(plugin == null)
			throw new TvheeAPIInternalException(getClass(), "enablePlugin", "Please call init() first!");

		if(plugin.isEnabled())
			return;

		try
		{
			plugin.onEnable();
			tvheeApiLogger.info("Enabled " + plugin.getDescription().getPluginName() + " " + plugin.getDescription().getVersion() + "!");
			plugin.setEnabled(true);
		}
		catch(Throwable e)
		{
			tvheeApiLogger.warning("Can't enable plugin " + plugin.getDescription().getPluginName() + " " + plugin.getDescription().getVersion() + "!");
			throw new TvheeAPIException(getClass(), "onPluginEnable", e);
		}
	}

	public void disablePlugin()
	{
		if(plugin == null)
			throw new IllegalArgumentException("Please call init() first!");

		if(!plugin.isEnabled())
			return;

		try
		{
			plugin.onDisable();
			tvheeApiLogger.info("Disabled " + plugin.getDescription().getPluginName() + " " + plugin.getDescription().getVersion() + "!");
			plugin.setEnabled(false);
		}
		catch(Throwable e)
		{
			tvheeApiLogger.warning("Can't disable plugin " + plugin.getDescription().getPluginName() + " " + plugin.getDescription().getVersion() + "!");
			throw new TvheeAPIException(getClass(), "onPluginDisable", e);
		}
	}

	public <T> T checkClass(String clazz, Class<T> expected, String falseMessage)
	{
		if(clazz == null)
			return null;

		try
		{
			Class<?> commandClass = Class.forName(clazz);
			Class<? extends T> apiCommand = commandClass.asSubclass(expected);
			Constructor<?> constructor = apiCommand.getConstructor();
			constructor.setAccessible(true);
			return Reflection.newInstance(apiCommand).getObject();
		}
		catch(ClassNotFoundException e)
		{
			throw new TvheeAPIException(getClass(), "onEnable", e);
		}
		catch(ClassCastException e)
		{
			throw new TvheeAPIException(getClass(), "onEnable", falseMessage.replaceAll("%expected%", expected.toString()).replaceAll("%class%", clazz));
		}
		catch(NoSuchMethodException e)
		{
			throw new TvheeAPIException(getClass(), "onEnable", "Class " + clazz + " must have a no args constructor!");
		}
	}

	private PluginDescription loadDescription(InputStream pluginFile)
	{
		try
		{
			Map<String, Object> yml = new Yaml().load(pluginFile);

			String name = (String) yml.get("name");
			PluginLoader JavaPlugin = PluginLoader.fromString((String) yml.get("main"));
			String apiMain = (String) yml.get("api-main");
			String version = (String) yml.get("version");
			ApiVersion apiVersion = ApiVersion.fromString((String) yml.get("api-version"));

			String description = null;
			if(yml.containsKey("description"))
				description = (String) yml.get("description");

			PluginLoadOrder pluginLoadOrder = PluginLoadOrder.fromString((String) yml.get("load"));

			List<String> authors = new ArrayList<>();
			if(yml.containsKey("authors"))
				authors = (List<String>) yml.get("authors");

			String author = null;
			if(yml.containsKey("author"))
				author = (String) yml.get("author");

			if(author != null)
				authors.add(author);

			String website = null;
			if(yml.containsKey("website"))
				website = (String) yml.get("website");

			String logPrefix = null;
			if(yml.containsKey("prefix"))
				logPrefix = (String) yml.get("prefix");

			List<String> dependencies = new ArrayList<>();
			if(yml.containsKey("depend"))
				dependencies = (List<String>) yml.get("depend");

			List<String> softDependencies = new ArrayList<>();
			if(yml.containsKey("softdepend"))
				softDependencies = (List<String>) yml.get("softdepend");

			List<String> loadBefore = new ArrayList<>();
			if(yml.containsKey("loadbefore"))
				loadBefore = (List<String>) yml.get("loadbefore");

			List<String> bungeeListeners = new ArrayList<>();

			if(yml.containsKey("listeners-bungee"))
				bungeeListeners = (List<String>) yml.get("listeners-bungee");

			List<String> spigotListeners = new ArrayList<>();

			if(yml.containsKey("listeners-spigot"))
				spigotListeners = (List<String>) yml.get("listeners-spigot");

			List<String> registeredCommands = new ArrayList<>();

			if(yml.containsKey("registered-commands"))
				registeredCommands = (List<String>) yml.get("registered-commands");

			return new PluginDescription(name, version, apiVersion, description, pluginLoadOrder, authors.toArray(new String[0]), website, apiMain, JavaPlugin, dependencies.toArray(new String[0]), logPrefix, softDependencies.toArray(new String[0]), loadBefore.toArray(new String[0]), bungeeListeners.toArray(new String[0]), spigotListeners.toArray(new String[0]), registeredCommands.toArray(new String[0]));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private TvheeAPIPlugin init(String main)
	{
		try
		{
			Class<?> jarClass;

			try
			{
				jarClass = Class.forName(main);
			}
			catch(ClassNotFoundException e)
			{
				throw new IllegalArgumentException("Cannot find main class '" + main + "'", e);
			}

			Class<? extends TvheeAPIPlugin> pluginClass;

			try
			{
				pluginClass = jarClass.asSubclass(TvheeAPIPlugin.class);
			}
			catch(ClassCastException e)
			{
				throw new IllegalArgumentException("Main class '" + main + "' does not extend TvheeAPIPlugin", e);
			}

			return pluginClass.getDeclaredConstructor().newInstance();
		}
		catch(InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return null;
	}
}
