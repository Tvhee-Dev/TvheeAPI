package me.tvhee.tvheeapi.api.command;

import java.util.List;
import me.tvhee.tvheeapi.api.player.Player;

public interface CommandExecutor
{
	CommandInformation getCommandInformation();

	void execute(CommandSender sender, Arguments arguments);

	List<String> tabComplete(Player player, Arguments arguments);
}
