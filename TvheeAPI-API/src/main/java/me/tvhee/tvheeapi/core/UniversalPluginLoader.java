package me.tvhee.tvheeapi.core;

import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;

public interface UniversalPluginLoader
{
	InputStream getDescriptionResource();

	InputStream getResource(String name);

	File getDataFolder();

	Logger getDefaultLogger();

	Logger getServerLogger();

	boolean isBukkit();

	boolean isBungee();
}
