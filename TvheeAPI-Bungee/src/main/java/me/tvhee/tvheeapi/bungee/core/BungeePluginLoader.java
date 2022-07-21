package me.tvhee.tvheeapi.bungee.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.tvhee.simplereflection.Reflection;
import me.tvhee.simplereflection.ReflectionUtil;
import me.tvhee.tvheeapi.api.command.BasicCommandExecutor;
import me.tvhee.tvheeapi.api.command.CommandExecutor;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.command.SimpleCommandExecutor;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.file.CustomFile;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;
import me.tvhee.tvheeapi.bungee.api.event.BungeeListener;
import me.tvhee.tvheeapi.core.TvheeAPIModule;
import me.tvhee.tvheeapi.core.TvheeAPIPluginLoader;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.yaml.snakeyaml.Yaml;

public final class BungeePluginLoader extends Plugin implements TvheeAPIModule
{
	private static boolean instanceCreated = false;
	private final PluginManager pluginManager;
	private TvheeAPIPluginLoader tvheeAPIPluginLoader;

	public BungeePluginLoader()
	{
		if(instanceCreated)
			throw new TvheeAPIException(getClass(), "init", "Please do not create instances of PluginLoader(s)!");

		this.pluginManager = new BungeePluginManager(this);
		instanceCreated = true;
	}

	@Override
	public void onLoad()
	{
		try
		{
			CustomFile fileRemover = new CustomFile(getDataFolder().getParentFile(), "TvheeAPI-FileRemover.txt");

			if(fileRemover.exists())
			{
				BufferedReader bufferedReader = new BufferedReader(new FileReader(fileRemover));
				String filePath = bufferedReader.readLine();
				bufferedReader.close();

				CustomFile oldJarFile = new CustomFile(filePath);
				oldJarFile.delete();
				fileRemover.delete();
			}
		}
		catch(Exception e)
		{
			throw new TvheeAPIInternalException(getClass(), "onLoad", e);
		}

		this.tvheeAPIPluginLoader = new TvheeAPIPluginLoader(this);
		tvheeAPIPluginLoader.init();
		tvheeAPIPluginLoader.getApiLogger().info("Is successfully loaded!");
		tvheeAPIPluginLoader.loadPlugin();
	}

	@Override
	public void onEnable()
	{
		tvheeAPIPluginLoader.enablePlugin();

		for(String bungeeListener : tvheeAPIPluginLoader.getDescription().getBungeeListeners())
		{
			Listener listener = tvheeAPIPluginLoader.checkClass(bungeeListener, Listener.class, "Class %class% must extend " + BungeeListener.class + " or implement %expected%!");
			pluginManager.registerListener(listener);
		}

		for(String command : tvheeAPIPluginLoader.getDescription().getRegisteredCommands())
		{
			CommandExecutor commandExecutor = tvheeAPIPluginLoader.checkClass(command, CommandExecutor.class, "Class %class% must implement %expected%, or extend " + SimpleCommandExecutor.class + " / " + BasicCommandExecutor.class + "!");
			pluginManager.registerCommand(commandExecutor);
		}

		tvheeAPIPluginLoader.getApiLogger().info("Is successfully enabled!");
	}

	@Override
	public void onDisable()
	{
		tvheeAPIPluginLoader.getApiLogger().info("Is successfully disabled!");
		tvheeAPIPluginLoader.disablePlugin();
	}

	@Override
	public String toString()
	{
		return "TvheeAPIPlugin {" + getDescription().getName() + " " + getDescription().getVersion() + "}";
	}

	@Override
	public InputStream getDescriptionResource()
	{
		return getResourceAsStream("bungee.yml");
	}

	@Override
	public InputStream getResource(String name)
	{
		return getResourceAsStream(name);
	}

	@Override
	public Logger getDefaultLogger()
	{
		return getLogger();
	}

	@Override
	public Logger getServerLogger()
	{
		return getProxy().getLogger();
	}

	@Override
	public boolean isBukkit()
	{
		return false;
	}

	@Override
	public boolean isBungee()
	{
		return true;
	}

	@Override
	public Configuration getConfiguration()
	{
		return new BungeeConfiguration();
	}

	@Override
	public Player getPlayer(String name)
	{
		ProxiedPlayer player = getProxy().getPlayer(name);

		if(player == null)
			return null;

		return new BungeePlayer(player);
	}

	@Override
	public Player getPlayer(UUID uuid)
	{
		ProxiedPlayer player = getProxy().getPlayer(uuid);

		if(player == null)
			return null;

		return new BungeePlayer(player);
	}

	@Override
	public PluginManager getPluginManager()
	{
		return pluginManager;
	}

	@Override
	public ConsoleCommandSender getConsoleSender()
	{
		return new BungeeConsoleSender(getProxy().getConsole());
	}

	@Override
	public Scheduler getScheduler()
	{
		return new BungeeScheduler(this, getProxy().getScheduler());
	}

	@Override
	public List<Player> getOnlinePlayers()
	{
		List<Player> onlinePlayers = new ArrayList<>();

		for(ProxiedPlayer bungeePlayer : getProxy().getPlayers())
			onlinePlayers.add(new BungeePlayer(bungeePlayer));

		return onlinePlayers;
	}

	@Override
	public void updatePluginFile(CustomFile newFile)
	{
		Reflection bungeePluginLoader = new Reflection(getProxy().getPluginManager());
		getProxy().getPluginManager().unregisterListeners(this);
		getProxy().getPluginManager().unregisterCommands(this);
		onDisable();

		try
		{
			for(Handler handler : getLogger().getHandlers())
				handler.close();
		}
		catch(Throwable t)
		{
			getProxy().getLogger().log(Level.SEVERE, "Exception disabling plugin " + getDescription().getName(), t);
		}

		Map<String, Plugin> loadedPlugins = bungeePluginLoader.field("plugins").object();
		loadedPlugins.remove(getDescription().getName());
		bungeePluginLoader.field("plugins", loadedPlugins);

		File[] jarFiles = getDataFolder().getParentFile().listFiles(pathname -> pathname.getName().endsWith(".jar"));

		if(jarFiles == null)
			jarFiles = new File[0];

		for(File file : jarFiles)
		{
			if(!file.getName().equals(newFile.getName()) || !file.isFile())
				continue;

			try
			{
				try(JarFile jar = new JarFile(file))
				{
					JarEntry pdf = jar.getJarEntry("bungee.yml");

					if(pdf == null)
						pdf = jar.getJarEntry("plugin.yml");

					if(pdf == null)
						throw new NullPointerException("Plugin must have a plugin.yml or bungee.yml");

					PluginDescription pluginDescription = ((Yaml) bungeePluginLoader.field("yaml").object()).loadAs(jar.getInputStream(pdf), PluginDescription.class);

					if(pluginDescription.getName() == null || pluginDescription.getMain() == null)
						throw new NullPointerException("Plugin from " + file + " must have a main and a name!");

					pluginDescription.setFile(file);

					Reflection pluginClassLoader = new Reflection(ReflectionUtil.getClass("net.md_5.bungee.api.plugin.PluginClassloader"));
					Set<?> allLoaders = new HashSet<>(pluginClassLoader.field("allLoaders").object());
					allLoaders.remove(getClass().getClassLoader());
					pluginClassLoader.field("allLoaders", allLoaders);
					((URLClassLoader) getClass().getClassLoader()).close();

					if(!(boolean) bungeePluginLoader.method("enablePlugin", new HashMap<>(), new Stack<>(), pluginDescription).object())
						getProxy().getLogger().log(Level.WARNING, "Failed to enable {0}", pluginDescription.getName());

					Map<String, Plugin> plugins = bungeePluginLoader.field("plugins").object();

					for(Entry<String, Plugin> entry : plugins.entrySet())
					{
						if(!entry.getKey().equals(pluginDescription.getName()))
							continue;

						Plugin plugin = entry.getValue();

						try
						{
							plugin.onEnable();
							ProxyServer.getInstance().getLogger().log(Level.INFO, "Enabled plugin {0} version {1} by {2}", new Object[] {plugin.getDescription().getName(), plugin.getDescription().getVersion(), plugin.getDescription().getAuthor()});
						}
						catch(Throwable e)
						{
							ProxyServer.getInstance().getLogger().log(Level.WARNING, "Exception encountered when loading plugin: " + plugin.getDescription().getName(), e);
						}
					}
				}
			}
			catch(Exception ex)
			{
				getProxy().getLogger().log(Level.WARNING, "Could not load plugin from file " + file, ex);
			}
		}

		getProxy().getScheduler().cancel(this);
	}
}
