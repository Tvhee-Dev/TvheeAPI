package me.tvhee.tvheeapi.api.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class ResourceFile extends CustomFile
{
	private final InputStream resource;

	public ResourceFile(ResourceFile resourceFile)
	{
		super(resourceFile);
		this.resource = resourceFile.resource;
	}

	public ResourceFile(String filePath, String resourcePath)
	{
		super(filePath);
		this.resource = getResource(resourcePath);
	}

	public ResourceFile(String parent, String child, String resourcePath)
	{
		super(parent, child);
		this.resource = getResource(resourcePath);
	}

	public ResourceFile(File parent, String child, String resourcePath)
	{
		super(parent, child);
		this.resource = getResource(resourcePath);
	}

	public ResourceFile(URI uri, String resourcePath)
	{
		super(uri);
		this.resource = getResource(resourcePath);
	}

	public ResourceFile(File file, String resourcePath)
	{
		super(file);
		this.resource = getResource(resourcePath);
	}

	public ResourceFile(CustomFile file, String resourcePath)
	{
		super(file);
		this.resource = getResource(resourcePath);
	}

	public final void saveResourceContent()
	{
		saveResourceContent(false);
	}

	public final void saveResourceContent(boolean replaceExisting)
	{
		if(!exists())
			createNewFile();

		if(!replaceExisting && this.length() != 0)
			return;

		try
		{
			Files.copy(resource, toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public final List<String> getResourceContent()
	{
		List<String> lines = new ArrayList<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(resource));

		try
		{
			String nextLine;
			while((nextLine = reader.readLine()) != null)
				lines.add(nextLine);

			reader.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return lines;
	}

	private InputStream getResource(String resource)
	{
		if(resource == null)
			throw new IllegalArgumentException("Resource cannot be null");

		InputStream inputStream = ResourceFile.class.getClassLoader().getResourceAsStream(resource);

		if(inputStream == null)
		{
			URL url = ResourceFile.class.getClassLoader().getResource(resource);

			if(url != null)
			{
				try
				{
					URLConnection connection = url.openConnection();
					connection.setUseCaches(false);
					connection.getInputStream();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		if(inputStream == null)
			throw new IllegalArgumentException("Couldn't find resource " + resource + ". Did you included it?");

		return inputStream;
	}
}
