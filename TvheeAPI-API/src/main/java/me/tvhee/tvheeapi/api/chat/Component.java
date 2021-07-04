package me.tvhee.tvheeapi.api.chat;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.tvhee.tvheeapi.api.TvheeAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public final class Component
{
	private String text;
	private boolean center;
	private boolean prefixed;

	public Component()
	{
		this.text = "";
	}

	public Component(String text)
	{
		this.text = checkText(text, "&", "§", "\u00a7");
	}

	public Component(boolean center)
	{
		this.center = center;
	}

	public Component(String text, boolean prefixed)
	{
		this(text);
		this.prefixed = prefixed;
	}

	public Component(Component component, boolean prefixed)
	{
		this(component);
		this.prefixed = prefixed;
	}

	public Component(String text, boolean prefixed, boolean center)
	{
		this(text);
		this.prefixed = prefixed;
		this.center = center;
	}

	public Component(Component text, boolean prefixed, boolean center)
	{
		this(text);
		this.prefixed = prefixed;
		this.center = center;
	}
	
	public Component(Component parent)
	{
		this.text = checkText(parent.text, "&", "§", "\u00a7");
		this.prefixed = parent.prefixed;
		this.center = parent.center;
	}

	public boolean isPrefixed()
	{
		return prefixed;
	}

	public void setPrefixed(boolean prefixed)
	{
		this.prefixed = prefixed;
	}

	public boolean isCentered()
	{
		return center;
	}

	public void setCentered(boolean center)
	{
		this.center = center;
	}

	public Component setText(String text)
	{
		this.text = text;
		return this;
	}

	public Component add(Component component)
	{
		return duplicate().setText(this.text + component.text);
	}

	public Component add(String text)
	{
		return duplicate().setText(this.text + text);
	}

	public Component addAtStart(Component component)
	{
		return duplicate().setText(component.text + this.text);
	}

	public Component addAtStart(String text)
	{
		return duplicate().setText(text + this.text);
	}

	public String getText()
	{
		if(this.text.equals(""))
			return null;

		return this.text;
	}

	public TextComponent toChatComponent(MessageType centerType)
	{
		return new TextComponent(toLegacyText(centerType));
	}

	public Component duplicate()
	{
		return new Component(this);
	}
	
	public String toLegacyText(MessageType centerType)
	{
		String toTranslate = text;
		Component prefix = TvheeAPI.getInstance().getPrefix();

		if(prefix != null && prefixed)
			toTranslate = prefix.text + text;

		if(centerType == null)
			return format(toTranslate);

		return center ? center(format(toTranslate), centerType) : format(toTranslate);
	}

	public String[] split(String regex)
	{
		return this.text.split(regex);
	}

	public Component replaceAll(String regex, String replacement)
	{
		this.text = this.text.replaceAll(regex, replacement);
		return this;
	}

	private boolean supportsHex()
	{
		try
		{
			ChatColor.class.getMethod("of", Color.class);
			return true;
		}
		catch(NoSuchMethodException e)
		{
			return false;
		}
	}

	private String checkText(String text, String... colorCodes)
	{
		if(text == null)
			text = "";

		Component prefix = TvheeAPI.getInstance().getPrefix();

		if(prefix != null)
		{
			String legacyPrefix = TvheeAPI.getInstance().getPrefix().toLegacyText(null);

			if(text.startsWith(legacyPrefix))
			{
				this.prefixed = true;
				text = text.replaceAll(legacyPrefix, "");
			}
		}

		if(text.startsWith(" "))
		{
			int spaces = 0;

			for(char character : text.toCharArray())
			{
				if(character == ' ')
					spaces++;
				else
					break;
			}

			this.text = text.substring(spaces);
			int pixels = (spaces * 2) + (text.toCharArray().length - spaces);

			if(pixels == MessageType.CHAT_LINE.getCenterPixels())
				this.center = true;
			else if(pixels == MessageType.MOTD_LINE.getCenterPixels())
				this.center = true;
			else if(pixels == MessageType.INVENTORY_LINE.getCenterPixels())
				this.center = true;
		}
		else if(text.endsWith(" "))
		{
			int spaces = 0;
			int length = text.toCharArray().length;

			for(int i = length; i >= 0; i = i - 1)
			{
				char character = text.toCharArray()[i];

				if(character == ' ')
					spaces++;
				else
					break;
			}

			this.text = text.trim();
			int pixels = (spaces * 2) + (length - spaces);

			if(pixels == MessageType.PROTOCOL_LINE.getCenterPixels())
				this.center = true;
		}
		
		for(String code : colorCodes)
		{
			if(!text.contains(code))
				continue;

			this.getInvalidColors(text, code);
		}

		return text;
	}

	private void getInvalidColors(String text, String code)
	{
		Pattern colorCodesPattern = Pattern.compile("(?<!\\\\)(" + code + "[a-zA-Z0-9])");
		Pattern validColorPattern = Pattern.compile("(?<!\\\\)(" + code + "[a-fk-orA-FK-OR0-9])");

		Matcher matcher = colorCodesPattern.matcher(text);

		while(matcher.find())
		{
			String color = text.substring(matcher.start(), matcher.end());
			if(!color.matches(validColorPattern.pattern()))
				throw new IllegalArgumentException("Invalid color code: " + color);
		}
	}

	private String center(String message, MessageType messageType)
	{
		if(message == null)
			return "null";

		String newMessage = this.format(message);

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;

		for(char character : newMessage.toCharArray())
		{
			if(character == ChatColor.COLOR_CHAR)
				previousCode = true;

			else if(previousCode)
			{
				previousCode = false;
				isBold = (character == 'l') || (character == 'L');
			}
			else
			{
				DefaultFontInfo defaultFontInfo = DefaultFontInfo.getDefaultFontInfo(character);
				messagePxSize += isBold ? defaultFontInfo.getBoldLength() : defaultFontInfo.getLength();
				messagePxSize++;
			}
		}

		int halvedMessageSize = messagePxSize / 2;
		double toCompensate = messageType.getCenterPixels() - halvedMessageSize;
		int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
		int compensated = 0;

		StringBuilder messageBuilder = new StringBuilder();

		if(messageType.isReversed())
			messageBuilder.append(newMessage);

		while(compensated < toCompensate)
		{
			messageBuilder.append(" ");
			compensated += spaceLength;
		}

		if(messageType.isReversed())
			return messageBuilder.toString();
		else
			return messageBuilder + newMessage;
	}

	private String format(String message)
	{
		String translatedMsg = message;

		if(message == null)
			return "null";

		if(this.supportsHex())
		{
			Matcher matcher = Pattern.compile("(?<!\\\\)(#[a-fA-F0-9]{6})").matcher(message);

			while(matcher.find())
			{
				String color = message.substring(matcher.start(), matcher.end());
				translatedMsg = translatedMsg.replaceAll(color, "" + ChatColor.of(color));
			}
		}

		try
		{
			return (String) Class.forName("org.bukkit.ChatColor").getMethod("translateAlternateColorCodes").invoke('&', translatedMsg);
		}
		catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
		{
			return ChatColor.translateAlternateColorCodes('&', translatedMsg);
		}
	}
}
