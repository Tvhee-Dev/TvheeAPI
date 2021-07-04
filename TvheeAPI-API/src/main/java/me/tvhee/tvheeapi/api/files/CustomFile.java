package me.tvhee.tvheeapi.api.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;

public class CustomFile extends File
{
	public final TvheeAPI api = TvheeAPI.getInstance();

	public CustomFile(String filePath)
	{
		super(filePath);
	}

	public CustomFile(String parent, String child)
	{
		super(parent, child);
	}

	public CustomFile(File parent, String child)
	{
		super(parent, child);
	}

	public CustomFile(URI uri)
	{
		super(uri);
	}

	public CustomFile(File file)
	{
		super(file.getPath());
	}

	public void saveDefaultFile()
	{
		if(!exists())
		{
			if(isDirectory())
				mkdirs();
			else
				getParentFile().mkdirs();

			InputStream is = api.getResource(getName());

			if(is == null)
				return;

			try
			{
				Files.copy(is, toPath());
			}
			catch(IOException e)
			{
				throw new TvheeAPIInternalException(CustomFile.class, "copy", e);
			}
		}
	}

	public boolean isYaml()
	{
		return this instanceof YamlConfigFile;
	}

	public YamlConfigFile getAsYaml()
	{
		if(isYaml())
			return (YamlConfigFile) this;
		else
			return null;
	}
}
