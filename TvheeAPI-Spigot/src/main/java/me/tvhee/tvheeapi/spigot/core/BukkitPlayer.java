package me.tvhee.tvheeapi.spigot.core;

import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.player.Player;

public final class BukkitPlayer extends BukkitCommandSender implements Player
{
	private final org.bukkit.entity.Player bukkitPlayer;

	public BukkitPlayer(BukkitPluginLoader bukkitPluginLoader, org.bukkit.entity.Player bukkitPlayer)
	{
		super(bukkitPluginLoader, bukkitPlayer);
		this.bukkitPlayer = bukkitPlayer;
	}

	@Override
	public void chat(Component component)
	{
		this.bukkitPlayer.chat(component.toLegacyText(MessageType.CHAT_LINE));
	}

	@Override
	public void kick(Component component)
	{
		this.bukkitPlayer.kickPlayer(component.toLegacyText(MessageType.CHAT_LINE));
	}

	@Override
	public String getName()
	{
		return this.bukkitPlayer.getName();
	}

	@Override
	public String getDisplayName()
	{
		return this.bukkitPlayer.getDisplayName();
	}

	@Override
	public void setDisplayName(String name)
	{
		this.bukkitPlayer.setDisplayName(name);
	}

	@Override
	public UUID getUniqueId()
	{
		return this.bukkitPlayer.getUniqueId();
	}

	@Override
	public MainHand getMainHand()
	{
		return this.bukkitPlayer.getMainHand() == org.bukkit.inventory.MainHand.LEFT ? MainHand.LEFT : MainHand.RIGHT;
	}

	@Override
	public int getPing()
	{
		return this.bukkitPlayer.getPing();
	}

	@Override
	public int getViewDistance()
	{
		return this.bukkitPlayer.getClientViewDistance();
	}

	@Override
	public boolean isConsole()
	{
		return false;
	}
}
