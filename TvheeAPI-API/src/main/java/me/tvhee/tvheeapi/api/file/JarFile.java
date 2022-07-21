package me.tvhee.tvheeapi.api.file;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.List;

public class JarFile extends CustomFile
{
	private Process program;

	public JarFile(String filePath)
	{
		super(filePath);
	}

	public JarFile(String parent, String child)
	{
		super(parent, child);
	}

	public JarFile(File parent, String child)
	{
		super(parent, child);
	}

	public JarFile(URI uri)
	{
		super(uri);
	}

	public JarFile(File file)
	{
		super(file);
	}

	public JarFile(CustomFile file)
	{
		super(file);
	}

	@Override
	protected List<String> getSupportedTypes()
	{
		return Collections.singletonList(".jar");
	}

	public void startProgram()
	{
		startProgram("");
	}

	public void startProgram(String arguments)
	{
		if(program != null)
			throw new IllegalArgumentException("Program is already running!");

		try
		{
			program = Runtime.getRuntime().exec("java -jar " + getPath() + " " + arguments);
			program.waitFor();

			if(program.exitValue() != 0)
				System.out.println("Weird exit code: " + program.exitValue());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void stopProgram()
	{
		if(program == null)
			throw new IllegalArgumentException("Program is NOT running!");

		if(program.isAlive())
			program.destroy();
	}
}
