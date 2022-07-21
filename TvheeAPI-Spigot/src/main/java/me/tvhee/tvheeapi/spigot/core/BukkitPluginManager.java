package me.tvhee.tvheeapi.spigot.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import me.tvhee.simplereflection.Reflection;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.command.Arguments;
import me.tvhee.tvheeapi.api.command.CommandExecutor;
import me.tvhee.tvheeapi.api.command.CommandInformation;
import me.tvhee.tvheeapi.api.event.Event;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.player.Player;
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.api.updater.SpigotUpdateChecker;
import me.tvhee.tvheeapi.spigot.api.event.BukkitEvent;
import me.tvhee.tvheeapi.spigot.api.event.SpigotListener;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;

public final class BukkitPluginManager implements PluginManager
{
	private final BukkitPluginLoader bukkitPluginLoader;
	private final Map<CommandInformation, CommandExecutor> registeredCommands = new HashMap<>();
	private SpigotUpdateChecker spigotUpdateChecker;

	public BukkitPluginManager(BukkitPluginLoader bukkitPluginLoader)
	{
		this.bukkitPluginLoader = bukkitPluginLoader;
	}

	@Override
	public void registerCommand(CommandExecutor commandExecutor)
	{
		CommandInformation command = commandExecutor.getCommandInformation();

		for(Entry<CommandInformation, CommandExecutor> entry : registeredCommands.entrySet())
		{
			if(entry.getKey().equals(command) && entry.getValue().equals(commandExecutor))
				return;
		}

		org.bukkit.command.Command bukkitCommand = new org.bukkit.command.Command(command.getName())
		{
			@Override
			public boolean execute(CommandSender sender, String label, String[] args)
			{
				me.tvhee.tvheeapi.api.command.CommandSender apiSender = sender instanceof org.bukkit.entity.Player ? new BukkitPlayer(bukkitPluginLoader, (org.bukkit.entity.Player) sender) : new BukkitCommandSender(bukkitPluginLoader, sender);

				if(command.getPermission() != null && command.getNoPermissionMessage() != null && !apiSender.hasPermission(command.getPermission()))
					apiSender.sendMessage(command.getNoPermissionMessage());

				commandExecutor.execute(apiSender, new Arguments(args));
				return true;
			}

			@Override
			public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException
			{
				Player apiSender = sender instanceof org.bukkit.entity.Player ? new BukkitPlayer(bukkitPluginLoader, (org.bukkit.entity.Player) sender) : null;

				if(apiSender != null && command.getPermission() != null && command.getNoPermissionMessage() != null && !apiSender.hasPermission(command.getPermission()))
					apiSender.sendMessage(command.getNoPermissionMessage());

				return commandExecutor.tabComplete(apiSender, new Arguments(args));
			}
		};

		bukkitCommand.setAliases(command.getAliases());
		bukkitCommand.setPermission(command.getPermission());

		if(command.getNoPermissionMessage() != null)
			bukkitCommand.setPermissionMessage(command.getNoPermissionMessage().toLegacyText(MessageType.CHAT_LINE));

		SimpleCommandMap commandMap = new Reflection(this.bukkitPluginLoader.getServer()).field("commandMap").object();
		commandMap.clearCommands();
		commandMap.register(TvheeAPIPlugin.getInstance().getDescription().getPluginName(), bukkitCommand);

		this.registeredCommands.put(command, commandExecutor);
	}

	@Override
	public void registerListener(Object listener)
	{
		if(listener instanceof Listener)
			bukkitPluginLoader.getServer().getPluginManager().registerEvents((Listener) listener, bukkitPluginLoader);
		else
			throw new TvheeAPIException(getClass(), "registerListener", "Please implement " + Listener.class + " or extend " + SpigotListener.class);
	}

	@Override
	public Event callEvent(Event event)
	{
		if(event.runAsync())
			return this.callEventAsync(event);
		else
			return this.callEventSync(event);
	}

	@Override
	public Event callEventSync(Event event)
	{
		CompletableFuture<Event> futureEvent = new CompletableFuture<>();

		if(event instanceof BukkitEvent)
		{
			Bukkit.getServer().getScheduler().runTask(this.bukkitPluginLoader, () ->
			{
				Bukkit.getServer().getPluginManager().callEvent((BukkitEvent) event);

				futureEvent.complete(event);
			});
		}

		try
		{
			return futureEvent.get();
		}
		catch(InterruptedException | ExecutionException e)
		{
			throw new TvheeAPIInternalException(getClass(), "callEventSync", e);
		}
	}

	@Override
	public Event callEventAsync(Event event)
	{
		CompletableFuture<Event> futureEvent = new CompletableFuture<>();

		if(event instanceof BukkitEvent)
		{
			Bukkit.getServer().getScheduler().runTaskAsynchronously(this.bukkitPluginLoader, () ->
			{
				Bukkit.getServer().getPluginManager().callEvent((BukkitEvent) event);
				futureEvent.complete(event);
			});
		}

		try
		{
			return futureEvent.get();
		}
		catch(InterruptedException | ExecutionException e)
		{
			throw new TvheeAPIInternalException(getClass(), "callEventAsync", e);
		}
	}

	@Override
	public SpigotUpdateChecker getUpdateChecker()
	{
		return this.spigotUpdateChecker;
	}

	@Override
	public void setUpdateChecker(SpigotUpdateChecker spigotUpdateChecker)
	{
		this.spigotUpdateChecker = spigotUpdateChecker;
	}
}
