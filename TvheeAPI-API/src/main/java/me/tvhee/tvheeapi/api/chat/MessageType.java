package me.tvhee.tvheeapi.api.chat;

public enum MessageType
{
	CHAT_LINE(154, false), MOTD_LINE(127, false), PROTOCOL_LINE(127, true), INVENTORY_LINE(78, false); //, SCOREBOARD_LINE(24);

	private final int centerPixels;
	private final boolean reversed;

	MessageType(int centerPixels, boolean reversed)
	{
		this.centerPixels = centerPixels;
		this.reversed = reversed;
	}

	public boolean isReversed()
	{
		return reversed;
	}

	public int getCenterPixels()
	{
		return centerPixels;
	}
}
