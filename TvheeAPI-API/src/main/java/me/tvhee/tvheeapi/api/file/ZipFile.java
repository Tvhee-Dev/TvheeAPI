package me.tvhee.tvheeapi.api.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import me.tvhee.tvheeapi.api.exception.TvheeAPIException;

public class ZipFile extends CustomFile
{
	public ZipFile(String filePath)
	{
		super(filePath);
	}

	public ZipFile(String parent, String child)
	{
		super(parent, child);
	}

	public ZipFile(File parent, String child)
	{
		super(parent, child);
	}

	public ZipFile(URI uri)
	{
		super(uri);
	}

	public ZipFile(File file)
	{
		super(file);
	}

	public ZipFile(CustomFile file)
	{
		super(file);
	}

	@Override
	protected final List<String> getSupportedTypes()
	{
		return Arrays.asList("zip", "rar", "7z", "gzip");
	}

	public final void unzip(File targetFolder)
	{
		targetFolder.delete();
		targetFolder.mkdir();

		try(java.util.zip.ZipFile zip = new java.util.zip.ZipFile(this))
		{
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while(entries.hasMoreElements())
			{
				ZipEntry entry = entries.nextElement();
				File outFile = new File(targetFolder, entry.getName());

				if(entry.isDirectory())
				{
					outFile.mkdirs();
				}
				else
				{
					if(outFile.getParentFile() != null)
						outFile.getParentFile().mkdirs();

					try(InputStream is = zip.getInputStream(entry))
					{
						outFile.delete();
						Files.copy(is, outFile.toPath());
					}
				}
			}
		}
		catch(IOException e)
		{
			throw new TvheeAPIException(getClass(), "unzip", "Failed to unzip file " + getPath() + "!");
		}
	}
}
