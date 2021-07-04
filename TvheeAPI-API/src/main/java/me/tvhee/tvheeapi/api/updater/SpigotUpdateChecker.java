package me.tvhee.tvheeapi.api.updater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.exception.DebugMessage;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.files.CustomFile;
import me.tvhee.tvheeapi.api.plugin.TvheeAPIPlugin;
import me.tvhee.tvheeapi.api.scheduler.SchedulerTime;
import me.tvhee.tvheeapi.core.TvheeAPICore;

public abstract class SpigotUpdateChecker
{
	private final int resourceId;
	private final boolean autoUpdate;

	public SpigotUpdateChecker(int resourceId, boolean autoUpdate)
	{
		this.resourceId = resourceId;
		this.autoUpdate = autoUpdate;
	}

	public final boolean updatesAutomatically()
	{
		return autoUpdate;
	}

	public abstract void onUpdateAvailable(String oldVersion, String newVersion);

	public abstract void onUpdateDownloaded();

	public abstract void onUpdateFinished();

	public final UpdateState checkForUpdates()
	{
		try
		{
			if(resourceId < 10000 || resourceId > 9999999)
				return UpdateState.ERROR;

			HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
			connection.setRequestMethod("GET");

			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String newVersion = reader.readLine();
			final String currentVersion = TvheeAPIPlugin.getInstance().getDescription().getVersion();

			if(newVersion.isEmpty())
				return UpdateState.ERROR;

			if(isNewerVersion(currentVersion, newVersion) && !currentVersion.contains("SNAPSHOT") && !currentVersion.contains("DEV"))
			{
				onUpdateAvailable(currentVersion, newVersion);

				if(!autoUpdate)
					return UpdateState.AVAILABLE;

				final ReadableByteChannel channel;

				connection = (HttpURLConnection) new URL("https://api.spiget.org/v2/resources/" + resourceId + "/download").openConnection();
				connection.setRequestProperty("User-Agent", TvheeAPIPlugin.getInstance().getDescription().getPluginName());
				channel = Channels.newChannel(connection.getInputStream());

				final String path = DebugMessage.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				final String decodedPath = URLDecoder.decode(path, StandardCharsets.UTF_8);
				final CustomFile thisJarFile = new CustomFile(decodedPath);
				final CustomFile destination = new CustomFile(thisJarFile.getParentFile(), TvheeAPIPlugin.getInstance().getDescription().getPluginName() + " V" + newVersion + ".jar");
				final FileOutputStream output = new FileOutputStream(destination);

				output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
				output.flush();
				output.close();

				onUpdateDownloaded();

				TvheeAPI.getInstance().getScheduler().schedule(() -> ((TvheeAPICore) TvheeAPI.getInstance()).updatePluginFile(destination), 1, SchedulerTime.SECONDS);

				File fileRemover = new File(TvheeAPIPlugin.getInstance().getDataFolder().getParentFile(), "TvheeAPI-FileRemover.txt");

				FileWriter fileWriter = new FileWriter(fileRemover);
				fileWriter.write(thisJarFile.getAbsolutePath());
				fileWriter.close();

				onUpdateFinished();
				return UpdateState.INSTALLED;
			}
			else
			{
				return UpdateState.LATEST_VERSION;
			}
		}
		catch(final UnknownHostException ex)
		{
			TvheeAPIPlugin.getInstance().getLogger().info("Could not check for update from " + ex.getMessage());
		}
		catch(final IOException ex)
		{
			if(ex.getMessage().startsWith("Server returned HTTP response code:"))
				TvheeAPIPlugin.getInstance().getLogger().info("Could not check for update, SpigotMC site appears to be down (or unaccessible): " + ex.getMessage());
			else
				throw new TvheeAPIInternalException(getClass(), "download", "IOException performing update from SpigotMC.org check!");
		}
		catch(final Exception ex)
		{
			throw new TvheeAPIInternalException(getClass(), "download", "Unknown error performing update from SpigotMC.org check!");
		}

		return UpdateState.ERROR;
	}

	private boolean isNewerVersion(final String current, final String remote)
	{
		if(remote.contains("-LEGACY"))
			return false;

		String[] currParts = current.split("-")[0].split("\\.");
		String[] remoteParts = remote.split("-")[0].split("\\.");

		if(currParts.length != remoteParts.length)
		{
			final boolean olderIsLonger = currParts.length > remoteParts.length;
			final String[] modifiedParts = new String[olderIsLonger ? currParts.length : remoteParts.length];

			for(int i = 0; i < (olderIsLonger ? currParts.length : remoteParts.length); i++)
				modifiedParts[i] = olderIsLonger ? remoteParts.length > i ? remoteParts[i] : "0" : currParts.length > i ? currParts[i] : "0";

			if(olderIsLonger)
				remoteParts = modifiedParts;
			else
				currParts = modifiedParts;
		}

		for(int i = 0; i < currParts.length; i++)
		{
			if(Integer.parseInt(currParts[i]) > Integer.parseInt(remoteParts[i]))
				return false;

			if(Integer.parseInt(remoteParts[i]) > Integer.parseInt(currParts[i]))
				return true;
		}

		return false;
	}

	public enum UpdateState
	{
		INSTALLED, AVAILABLE, LATEST_VERSION, ERROR
	}
}
