package me.tvhee.tvheeapi.bungee.core;

import java.util.HashMap;
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
import me.tvhee.tvheeapi.api.plugin.PluginManager;
import me.tvhee.tvheeapi.api.updater.SpigotUpdateChecker;
import me.tvhee.tvheeapi.bungee.api.event.BungeeEvent;
import me.tvhee.tvheeapi.bungee.api.event.BungeeListener;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;

public final class BungeePluginManager implements PluginManager
{
	private final BungeePluginLoader bungeePluginLoader;
	private final Map<CommandInformation, CommandExecutor> registeredCommands = new HashMap<>();
	private SpigotUpdateChecker spigotUpdateChecker;

	public BungeePluginManager(BungeePluginLoader bungeePluginLoader)
	{
		this.bungeePluginLoader = bungeePluginLoader;
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

		net.md_5.bungee.api.plugin.Command bungeeCommand = new net.md_5.bungee.api.plugin.Command(command.getName(), command.getPermission(), command.getAliases().toArray(new String[0]))
		{
			@Override
			public void execute(CommandSender sender, String[] args)
			{
				me.tvhee.tvheeapi.api.command.CommandSender apiSender = sender instanceof ProxiedPlayer ? new BungeePlayer((ProxiedPlayer) sender) : new BungeeCommandSender(sender);

				if(command.getPermission() != null && command.getNoPermissionMessage() != null && !apiSender.hasPermission(command.getPermission()))
					apiSender.sendMessage(command.getNoPermissionMessage());

				commandExecutor.execute(apiSender, new Arguments(args));
			}
		};

		if(command.getNoPermissionMessage() != null)
			new Reflection(bungeeCommand).field("permissionMessage", command.getNoPermissionMessage().toLegacyText(MessageType.CHAT_LINE));

		bungeePluginLoader.getProxy().getPluginManager().registerCommand(bungeePluginLoader, bungeeCommand);
		registeredCommands.put(command, commandExecutor);
	}

	@Override
	public void registerListener(Object listener)
	{
		if(listener instanceof Listener)
			bungeePluginLoader.getProxy().getPluginManager().registerListener(bungeePluginLoader, (Listener) listener);
		else
			throw new TvheeAPIException(getClass(), "registerListener", "Please implement " + Listener.class + " or extend " + BungeeListener.class);
	}

	@Override
	public Event callEvent(Event event)
	{
		if(!(event instanceof BungeeEvent))
			return null;

		if(event.runAsync())
			return callEventAsync(event);
		else
			return callEventSync(event);
	}

	@Override
	public Event callEventSync(Event event)
	{
		if(!(event instanceof BungeeEvent))
			return null;

		CompletableFuture<Event> futureEvent = new CompletableFuture<>();

		ProxyServer.getInstance().getPluginManager().callEvent((BungeeEvent) event);
		futureEvent.complete(event);

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
		if(!(event instanceof BungeeEvent))
			return null;

		CompletableFuture<Event> futureEvent = new CompletableFuture<>();

		ProxyServer.getInstance().getScheduler().runAsync(bungeePluginLoader, () ->
		{
			ProxyServer.getInstance().getPluginManager().callEvent((BungeeEvent) event);
			futureEvent.complete(event);
		});

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
