package me.tvhee.tvheeapi.api.serverping;

import java.util.Objects;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.chat.MessageType;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;

public final class Motd
{
	private Component text1;
	private Component text2;

	public Motd(Component text)
	{
		Objects.requireNonNull(text);
		this.text1 = text;
	}

	public Motd(Component text, Component text2)
	{
		Objects.requireNonNull(text);
		Objects.requireNonNull(text2);
		this.text1 = text;
		this.text2 = text2;
	}

	public Component getTextLine1()
	{
		return text1;
	}

	private void setTextLine1(Component text)
	{
		Objects.requireNonNull(text);
		this.text1 = text;
	}

	public Component getTextLine2()
	{
		return text2;
	}

	private void setTextLine2(Component text)
	{
		Objects.requireNonNull(text);
		this.text2 = text;
	}

	public String toLegacy()
	{
		if(text1 == null && text2 == null)
			return "";
		else if(text1 != null && text2 == null)
			return text1.toLegacyText(MessageType.MOTD_LINE);
		else if(text1 == null)
			return "\n" + text2.toLegacyText(MessageType.MOTD_LINE);
		else
			return text1.toLegacyText(MessageType.MOTD_LINE) + "\n" + text2.toLegacyText(MessageType.MOTD_LINE);
	}

	public static Motd fromLegacy(String motd)
	{
		if(motd.startsWith("\n"))
			return new Motd(new Component(), new Component(motd.split("\n")[0]));

		String[] lines = motd.split("\n");
		Component[] components = new Component[lines.length];

		for(int i = 0; i < lines.length; i++)
			components[i] = new Component(lines[i]);

		switch(components.length)
		{
			case 1 -> {
				return new Motd(components[0]);
			}
			case 2 -> {
				if(components[0] != null)
					return new Motd(components[0], components[1]);
				else
					return new Motd(new Component(), new Component(components[1]));
			}
			default -> throw new TvheeAPIException(Motd.class, "fromString", "Not a valid motd!");
		}
	}
}
