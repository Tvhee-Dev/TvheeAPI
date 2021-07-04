package me.tvhee.tvheeapi.api.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;

public final class Arguments
{
	private final List<String> arguments = new ArrayList<>();

	public Arguments(String[] args)
	{
		this.arguments.addAll(Arrays.asList(args));
	}

	public String get(int position)
	{
		try
		{
			return arguments.get(position);
		}
		catch(ArrayIndexOutOfBoundsException ignored)
		{
			throw new TvheeAPIException(getClass(), "get", "Position is bigger then the size!");
		}
	}

	/**
	 *
	 * @param start
	 * @param end Fill in 0 to get all the args from start until the length
	 * @return
	 */

	public String get(int start, int end)
	{
		StringBuilder argsBuilder = new StringBuilder();

		if(end <= 0)
		{
			for(int i = start; i < arguments.size(); i++)
				argsBuilder.append(get(i));
		}
		else
		{
			for(int i = start; i < end; i++)
				argsBuilder.append(get(i));
		}

		return argsBuilder.toString();
	}

	public boolean hasLength(int length)
	{
		return arguments.size() >= length;
	}

	public boolean isEmpty()
	{
		return arguments.isEmpty();
	}
}
