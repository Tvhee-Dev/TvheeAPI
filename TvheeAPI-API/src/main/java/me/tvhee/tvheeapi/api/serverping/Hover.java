package me.tvhee.tvheeapi.api.serverping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import me.tvhee.tvheeapi.api.chat.Component;

public final class Hover
{
	private final Map<Component, UUID> hover = new HashMap<>();

	public Hover(Component... text)
	{
		for(Component line : text)
			hover.put(line, UUID.randomUUID());
	}

	public Hover(List<Component> text)
	{
		for(Component line : text)
			hover.put(line, UUID.randomUUID());
	}

	public Hover(Map<Component, UUID> hover)
	{
		this.hover.putAll(hover);
	}

	public void addLine(Component text)
	{
		this.hover.put(text, UUID.randomUUID());
	}

	public void addLine(Component text, UUID uuid)
	{
		this.hover.put(text, uuid);
	}

	public void removeLine(Component text)
	{
		this.hover.remove(text);
	}

	public Map<Component, UUID> getHoverLines()
	{
		return hover;
	}
}
