package me.tvhee.tvheeapi.api.command;

import java.util.List;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.scheduler.SchedulerTime;

public interface CommandSender
{
	void sendMessage(Component component);

	void sendMessage(Component... components);

	void sendMessage(List<Component> components);

	boolean hasPermission(String permission);

	boolean isConsole();

	boolean isOp();

	void setOp(boolean op);

	void addPermission(String permission);

	void addPermission(String permission, long time, SchedulerTime schedulerTime);

	void removePermission(String permission);

	void removePermission(String permission, long time, SchedulerTime schedulerTime);

	Object getOriginal();

	boolean isBukkit();

	boolean isBungee();
}
