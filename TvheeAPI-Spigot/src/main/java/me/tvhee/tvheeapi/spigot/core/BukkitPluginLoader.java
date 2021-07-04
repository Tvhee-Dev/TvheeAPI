package me.tvhee.tvheeapi.spigot.core;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import me.tvhee.tvheeapi.api.command.BasicCommandExecutor;
import me.tvhee.tvheeapi.api.command.CommandExecutor;
import me.tvhee.tvheeapi.api.command.ConsoleCommandSender;
import me.tvhee.tvheeapi.api.command.SimpleCommandExecutor;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.files.CustomFile;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.scheduler.Scheduler;
import me.tvhee.tvheeapi.core.TvheeAPILoader;
import me.tvhee.tvheeapi.core.TvheeAPIPluginLoader;
import me.tvhee.tvheeapi.spigot.api.crafting.CraftingRecipe;
import me.tvhee.tvheeapi.spigot.api.event.SpigotListener;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitPluginLoader extends JavaPlugin implements TvheeAPILoader
{
	private static boolean instanceCreated = false;
	private final PluginManager pluginManager;
	private TvheeAPIPluginLoader tvheeAPIPluginLoader;

	public BukkitPluginLoader()
	{
		if(instanceCreated)
			throw new TvheeAPIInternalException(getClass(), "init", "Please do not create instances of PluginLoader(s)!");

		pluginManager = new BukkitPluginManager(this);
		instanceCreated = true;
	}

	@Override
	public final void onLoad()
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
		this.tvheeAPIPluginLoader.init();
		this.tvheeAPIPluginLoader.getApiLogger().info("Is successfully loaded!");
		this.tvheeAPIPluginLoader.loadPlugin();
	}

	@Override
	public final void onEnable()
	{
		if(!tvheeAPIPluginLoader.getDescription().getApiVersion().isSupported())
		{
			this.tvheeAPIPluginLoader.getApiLogger().warning("-------------- WARNING ---------------");
			this.tvheeAPIPluginLoader.getApiLogger().warning("Version " + tvheeAPIPluginLoader.getDescription().getApiVersion() + " is not supported!");
			this.tvheeAPIPluginLoader.getApiLogger().warning("--------------------------------------");
		}

		this.tvheeAPIPluginLoader.enablePlugin();

		pluginManager.registerListener(new BukkitPingModifier());

		for(String spigotListener : tvheeAPIPluginLoader.getDescription().getSpigotListeners())
		{
			Listener listener = tvheeAPIPluginLoader.checkClass(spigotListener, Listener.class, "Class %class% must extend " + SpigotListener.class + " or implement %expected%!");
			pluginManager.registerListener(listener);
		}

		for(String command : tvheeAPIPluginLoader.getDescription().getRegisteredCommands())
		{
			CommandExecutor commandExecutor = tvheeAPIPluginLoader.checkClass(command, CommandExecutor.class, "Class %class% must implement %expected%, or extend " + SimpleCommandExecutor.class + " / " + BasicCommandExecutor.class + "!");
			pluginManager.registerCommand(commandExecutor);
		}

		this.tvheeAPIPluginLoader.getApiLogger().info("Is successfully enabled!");
	}

	@Override
	public final void onDisable()
	{
		CraftingRecipe.unregisterAll();

		this.tvheeAPIPluginLoader.getApiLogger().info("Is successfully disabled!");
		this.tvheeAPIPluginLoader.disablePlugin();
	}

	@Override
	public final String toString()
	{
		return "TvheeAPIPlugin {" + getDescription().getName() + " " + getDescription().getVersion() + "}";
	}

	@Override
	public InputStream getDescriptionResource()
	{
		return this.getResource("plugin.yml");
	}

	@Override
	public Logger getDefaultLogger()
	{
		return this.getLogger();
	}

	@Override
	public Logger getServerLogger()
	{
		return this.getServer().getLogger();
	}

	@Override
	public boolean isBukkit()
	{
		return true;
	}

	@Override
	public boolean isBungee()
	{
		return false;
	}

	@Override
	public Configuration getYamlConfiguration()
	{
		return new BukkitConfiguration();
	}

	@Override
	public Player getPlayer(String name)
	{
		org.bukkit.entity.Player bukkitPlayer = getServer().getPlayer(name);

		if(bukkitPlayer == null)
			return null;

		return new BukkitPlayer(this, bukkitPlayer);
	}

	@Override
	public Player getPlayer(UUID uuid)
	{
		org.bukkit.entity.Player bukkitPlayer = getServer().getPlayer(uuid);

		if(bukkitPlayer == null)
			return null;

		return new BukkitPlayer(this, bukkitPlayer);
	}

	@Override
	public PluginManager getPluginManager()
	{
		return pluginManager;
	}

	@Override
	public ConsoleCommandSender getConsoleSender()
	{
		return new BukkitConsoleSender(this, getServer().getConsoleSender());
	}

	@Override
	public Scheduler getScheduler()
	{
		return new BukkitScheduler(this, getServer().getScheduler());
	}

	@Override
	public Collection<Player> getOnlinePlayers()
	{
		List<Player> onlinePlayers = new ArrayList<>();

		for(org.bukkit.entity.Player bukkitPlayer : getServer().getOnlinePlayers())
			onlinePlayers.add(new BukkitPlayer(this, bukkitPlayer));

		return onlinePlayers;
	}

	@Override
	public void updatePluginFile(CustomFile newFile)
	{
		getServer().getPluginManager().disablePlugin(this);

		try
		{
			Plugin plugin = getServer().getPluginManager().loadPlugin(newFile);

			if(plugin == null)
				throw new TvheeAPIInternalException(getClass(), "updatePluginFile", "The new version does not exist!");

			plugin.onLoad();
			getServer().getPluginManager().enablePlugin(plugin);
		}
		catch(InvalidPluginException | InvalidDescriptionException e)
		{
			e.printStackTrace();
		}
	}
}
