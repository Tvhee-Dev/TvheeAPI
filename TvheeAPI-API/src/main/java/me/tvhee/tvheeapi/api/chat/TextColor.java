package me.tvhee.tvheeapi.api.chat;

import java.util.Random;
import net.md_5.bungee.api.ChatColor;

public enum TextColor
{
	BLACK(ChatColor.BLACK),
	DARK_BLUE(ChatColor.DARK_BLUE),
	BLUE(ChatColor.BLUE),
	DARK_RED(ChatColor.DARK_RED),
	AQUA(ChatColor.AQUA),
	DARK_GRAY(ChatColor.DARK_GRAY),
	DARK_GREEN(ChatColor.DARK_GREEN),
	DARK_AQUA(ChatColor.DARK_AQUA),
	GREEN(ChatColor.GREEN),
	DARK_PURPLE(ChatColor.DARK_PURPLE),
	GOLD(ChatColor.GOLD),
	LIGHT_PURPLE(ChatColor.LIGHT_PURPLE),
	LIGHT_GRAY(ChatColor.GRAY),
	WHITE(ChatColor.WHITE),
	YELLOW(ChatColor.YELLOW),
	RED(ChatColor.RED);

	private final ChatColor chatColor;

	TextColor(ChatColor chatColor)
	{
		this.chatColor = chatColor;
	}

	public ChatColor getChatColor()
	{
		return chatColor;
	}

	public static TextColor getRandom()
	{
		TextColor[] colors = values();
		return colors[new Random().nextInt(colors.length)];
	}

	@Override
	public String toString()
	{
		return chatColor.toString();
	}
}
