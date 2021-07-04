package me.tvhee.tvheeapi.api.serverping;

import java.awt.image.BufferedImage;
import me.tvhee.tvheeapi.api.chat.Component;

public abstract class ServerPing
{
	public abstract Motd getMotd();

	public abstract int getOnlinePlayers();

	public abstract int getMaxPlayers();

	public abstract Hover getHover();

	public abstract Component getProtocolName();

	public abstract int getProtocolVersion();

	public abstract BufferedImage getFavicon();
}
