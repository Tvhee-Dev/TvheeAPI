package me.tvhee.tvheeapi.api.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.tvhee.tvheeapi.api.chat.Component;
import me.tvhee.tvheeapi.api.command.CommandSender;
import me.tvhee.tvheeapi.api.config.Configuration;
import me.tvhee.tvheeapi.api.player.Player;

public final class Locale
{
	public static String messagePathPrefix = "messages.";
	private final List<MessageReplacer> replacers = new ArrayList<>();
	private final Configuration config;

	public Locale(Configuration config)
	{
		this.config = config;
	}

	public Configuration getConfig()
	{
		return config;
	}

	public void registerMessageReplacer(MessageReplacer replacer)
	{
		replacers.add(replacer);
	}

	public void unregisterMessageReplacer(MessageReplacer replacer)
	{
		replacers.add(replacer);
	}

	public Component getMessage(String messagePath, Object... placeholders)
	{
		return getMessage(LocalePrefix.NO_PREFIX, messagePath, null, placeholders);
	}

	public Component getMessage(String messagePath, CommandSender replaceFor, Object... placeholders)
	{
		return getMessage(LocalePrefix.NO_PREFIX, messagePath, replaceFor, placeholders);
	}

	public Component getMessage(LocalePrefix prefix, String messagePath, Object... placeholders)
	{
		return getMessage(prefix, messagePath, null, placeholders);
	}

	public Component getMessage(LocalePrefix prefix, String messagePath, CommandSender replaceFor, Object... placeholders)
	{
		List<Component> list = getMessageList(prefix, messagePath, replaceFor, placeholders);
		return list.isEmpty() ? new Component() : list.get(0);
	}

	public List<Component> getMessageList(String messagePath, Object... placeholders)
	{
		return getMessageList(LocalePrefix.NO_PREFIX, messagePath, null, placeholders);
	}

	public List<Component> getMessageList(String messagePath, CommandSender replaceFor, Object... placeholders)
	{
		return getMessageList(LocalePrefix.NO_PREFIX, messagePath, replaceFor, placeholders);
	}

	public List<Component> getMessageList(LocalePrefix prefix, String messagePath, Object... placeholders)
	{
		return getMessageList(prefix, messagePath, null, placeholders);
	}

	public List<Component> getMessageList(LocalePrefix prefix, String messagePath, CommandSender replaceFor, Object... placeholders)
	{
		return configMessage(prefix, messagePath, replaceFor, placeholders);
	}

	public void sendMessage(String messagePath, CommandSender receiver, Object... placeholders)
	{
		sendMessage(LocalePrefix.NO_PREFIX, messagePath, null, Collections.singletonList(receiver), placeholders);
	}

	public void sendMessage(String messagePath, List<? extends CommandSender> receivers, Object... placeholders)
	{
		sendMessage(LocalePrefix.NO_PREFIX, messagePath, null, receivers, placeholders);
	}

	public void sendMessage(LocalePrefix prefix, String messagePath, CommandSender receiver, Object... placeholders)
	{
		sendMessage(prefix, messagePath, null, Collections.singletonList(receiver), placeholders);
	}

	public void sendMessage(LocalePrefix prefix, String messagePath, List<? extends CommandSender> receivers, Object... placeholders)
	{
		sendMessage(prefix, messagePath, null, receivers, placeholders);
	}

	public void sendMessage(String messagePath, CommandSender replaceFor, CommandSender receiver, Object... placeholders)
	{
		sendMessage(LocalePrefix.NO_PREFIX, messagePath, replaceFor, Collections.singletonList(receiver), placeholders);
	}

	public void sendMessage(String messagePath, CommandSender replaceFor, List<? extends CommandSender> receivers, Object... placeholders)
	{
		sendMessage(LocalePrefix.NO_PREFIX, messagePath, replaceFor, receivers, placeholders);
	}

	public void sendMessage(LocalePrefix prefix, String messagePath, CommandSender replaceFor, CommandSender receiver, Object... placeholders)
	{
		sendMessage(prefix, messagePath, replaceFor, Collections.singletonList(receiver), placeholders);
	}

	public void sendMessage(LocalePrefix prefix, String messagePath, CommandSender replaceFor, List<? extends CommandSender> receivers, Object... placeholders)
	{
		List<Component> messages = configMessage(prefix, messagePath, replaceFor, placeholders);

		for(Component message : messages)
		{
			for(CommandSender receiver : receivers)
				receiver.sendMessage(message);
		}
	}

	private List<Component> configMessage(LocalePrefix prefix, String messagePath, CommandSender replaceFor, Object... placeholders)
	{
		if(!messagePath.startsWith(messagePathPrefix))
			messagePath = messagePathPrefix + messagePath;

		if(config.getString(messagePath, null) != null)
			return Collections.singletonList(replace(new Component(prefix + config.getString(messagePath)), replaceFor, placeholders));

		List<Component> messages = new ArrayList<>();

		for(String line : config.getStringList(messagePath))
			messages.add(replace(new Component(prefix + line), replaceFor, placeholders));

		return messages;
	}

	public Component replace(Component text, CommandSender target, Object... placeholders)
	{
		Map<String, String> placeholdersMapped = new HashMap<>();
		List<String> otherPlaceholders = new ArrayList<>();

		for(Object placeholder : placeholders)
		{
			if(placeholder instanceof MessageReplacer)
			{
				MessageReplacer replacer = (MessageReplacer) placeholder;
				text = replacer.replace(text, target);
			}
			else
				otherPlaceholders.add(placeholder.toString());
		}

		if(otherPlaceholders.size() % 2 != 0)
			throw new IllegalArgumentException(otherPlaceholders.get(otherPlaceholders.size() - 1) + " has no replacement defined!");

		for(int i = 0; i < otherPlaceholders.size(); i = i + 2)
		{
			String key = "%" + placeholders[i] + "%";
			String value = placeholders[i + 1] + "";

			placeholdersMapped.put(key, value);
		}

		if(target != null)
			placeholdersMapped.put("%player%", target instanceof Player ? ((Player) target).getName() : "");

		for(Map.Entry<String, String> entry : placeholdersMapped.entrySet())
			text.replaceAll(entry.getKey(), entry.getValue());

		for(MessageReplacer replacer : replacers)
			text = replacer.replace(text, target);

		return text;
	}

	public interface MessageReplacer
	{
		Component replace(Component message, CommandSender parameter);
	}
}
