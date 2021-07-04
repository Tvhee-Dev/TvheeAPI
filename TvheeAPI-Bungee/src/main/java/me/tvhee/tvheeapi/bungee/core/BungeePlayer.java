package me.tvhee.tvheeapi.bungee.core;

import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.player.Player;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public final class BungeePlayer extends BungeeCommandSender implements Player
{
	private final net.md_5.bungee.api.connection.ProxiedPlayer bungeePlayer;

	public BungeePlayer(ProxiedPlayer bungeePlayer)
	{
		super(bungeePlayer);
		this.bungeePlayer = bungeePlayer;
	}

	@Override
	public void chat(Component component)
	{
		this.bungeePlayer.chat(new Component(component).toLegacyText(MessageType.CHAT_LINE));
	}

	@Override
	public void kick(Component component)
	{
		bungeePlayer.disconnect(component.toChatComponent(MessageType.CHAT_LINE));
	}

	@Override
	public String getName()
	{
		return this.bungeePlayer.getName();
	}

	@Override
	public String getDisplayName()
	{
		return this.bungeePlayer.getDisplayName();
	}

	@Override
	public void setDisplayName(String name)
	{
		this.bungeePlayer.setDisplayName(name);
	}

	@Override
	public UUID getUniqueId()
	{
		return this.bungeePlayer.getUniqueId();
	}

	@Override
	public MainHand getMainHand()
	{
		return this.bungeePlayer.getMainHand() == ProxiedPlayer.MainHand.LEFT ? MainHand.LEFT : MainHand.RIGHT;
	}

	@Override
	public int getPing()
	{
		return this.bungeePlayer.getPing();
	}

	@Override
	public int getViewDistance()
	{
		return this.bungeePlayer.getViewDistance();
	}
}
