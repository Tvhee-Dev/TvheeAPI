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
import me.tvhee.tvheeapi.api.TvheeAPI;
import me.tvhee.tvheeapi.api.command.CommandSender;
import me.tvhee.tvheeapi.api.exception.DebugMessage;
import me.tvhee.tvheeapi.api.exception.TvheeAPIInternalException;
import me.tvhee.tvheeapi.api.file.CustomFile;
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

	public abstract void onUpdateAvailable(CommandSender sender, String oldVersion, String newVersion);

	public abstract void onUpdateDownloaded(CommandSender sender);

	public abstract void onUpdateFinished(CommandSender sender);

	public final UpdateState checkForUpdates(CommandSender sender)
	{
		try
		{
			if(resourceId < 10000 || resourceId > 9999999)
				return UpdateState.ERROR;

			HttpURLConnection connection = (HttpURLConnection) new URL("https://api.spigotmc.org/legacy/update.php?resource=" + resourceId).openConnection();
			connection.setRequestMethod("GET");

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String newVersion = reader.readLine();
			String currentVersion = TvheeAPIPlugin.getInstance().getDescription().getVersion();

			if(newVersion.isEmpty())
				return UpdateState.ERROR;

			if(isNewerVersion(currentVersion, newVersion) && !currentVersion.contains("SNAPSHOT") && !currentVersion.contains("DEV"))
			{
				onUpdateAvailable(sender, currentVersion, newVersion);

				if(!autoUpdate)
					return UpdateState.AVAILABLE;

				connection = (HttpURLConnection) new URL("https://api.spiget.org/v2/resources/" + resourceId + "/download").openConnection();
				connection.setRequestProperty("User-Agent", TvheeAPIPlugin.getInstance().getDescription().getPluginName());
				ReadableByteChannel channel = Channels.newChannel(connection.getInputStream());

				String path = DebugMessage.class.getProtectionDomain().getCodeSource().getLocation().getPath();
				String decodedPath = URLDecoder.decode(path, "UTF-8");
				CustomFile thisJarFile = new CustomFile(decodedPath);
				CustomFile destination = new CustomFile(thisJarFile.getParentFile(), TvheeAPIPlugin.getInstance().getDescription().getPluginName() + " V" + newVersion + ".jar");
				FileOutputStream output = new FileOutputStream(destination);

				output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
				output.flush();
				output.close();

				onUpdateDownloaded(sender);

				TvheeAPI.getInstance().getScheduler().schedule(() -> ((TvheeAPICore) TvheeAPI.getInstance()).updatePluginFile(destination), 1, SchedulerTime.SECONDS);
				File fileRemover = new File(TvheeAPIPlugin.getInstance().getDataFolder().getParentFile(), "TvheeAPI-FileRemover.txt");

				FileWriter fileWriter = new FileWriter(fileRemover);
				fileWriter.write(thisJarFile.getAbsolutePath());
				fileWriter.close();

				onUpdateFinished(sender);
				return UpdateState.INSTALLED;
			}
			else
			{
				return UpdateState.LATEST_VERSION;
			}
		}
		catch(UnknownHostException ex)
		{
			TvheeAPIPlugin.getInstance().getLogger().info("Could not check for update from " + ex.getMessage());
		}
		catch(IOException ex)
		{
			if(ex.getMessage().startsWith("Server returned HTTP response code:"))
				TvheeAPIPlugin.getInstance().getLogger().info("Could not check for update, SpigotMC site appears to be down (or unaccessible): " + ex.getMessage());
			else
				throw new TvheeAPIInternalException(getClass(), "download", "IOException performing update from SpigotMC.org check!");
		}
		catch(Exception ex)
		{
			throw new TvheeAPIInternalException(getClass(), "download", "Unknown error performing update from SpigotMC.org check!");
		}

		return UpdateState.ERROR;
	}

	protected boolean isNewerVersion(String current, String remote)
	{
		String[] currParts = current.split("-")[0].split("\\.");
		String[] remoteParts = remote.split("-")[0].split("\\.");

		if(currParts.length != remoteParts.length)
		{
			boolean olderIsLonger = currParts.length > remoteParts.length;
			String[] modifiedParts = new String[olderIsLonger ? currParts.length : remoteParts.length];

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
