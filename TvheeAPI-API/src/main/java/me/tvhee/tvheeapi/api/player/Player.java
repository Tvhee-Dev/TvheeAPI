package me.tvhee.tvheeapi.api.player;

import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.command.CommandSender;

public interface Player extends CommandSender
{
	void chat(Component component);

	void kick(Component component);

	String getName();

	String getDisplayName();

	void setDisplayName(String name);

	UUID getUniqueId();

	MainHand getMainHand();

	int getPing();

	int getViewDistance();

	default boolean isConsole()
	{
		return false;
	}

	enum MainHand
	{
		LEFT, RIGHT
	}
}
