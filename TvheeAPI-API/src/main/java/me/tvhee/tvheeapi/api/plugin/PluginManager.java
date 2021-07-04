package me.tvhee.tvheeapi.api.plugin;

import me.tvhee.tvheeapi.api.command.CommandExecutor;
import me.tvhee.tvheeapi.api.event.Event;
import me.tvhee.tvheeapi.api.serverping.ServerPing;
import me.tvhee.tvheeapi.api.updater.SpigotUpdateChecker;

public interface PluginManager
{
	void registerCommand(CommandExecutor commandExecutor);

	void registerListener(Object listener);

	Event callEvent(Event event);

	Event callEventSync(Event event);

	Event callEventAsync(Event event);

	ServerPing getServerPing();

	void setServerPing(ServerPing serverPing);

	SpigotUpdateChecker getUpdateChecker();

	void setUpdateChecker(SpigotUpdateChecker spigotUpdateChecker);
}
