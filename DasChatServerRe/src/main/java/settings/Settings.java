package settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

import constants.Paths;
import logger.DasChatLogger;

public class Settings
{
	private static Settings	instance;

	private int				port;

	public int getPort()
	{
		return port;
	}

	public void setPort(int port)
	{
		this.port = port;
		DasChatLogger.getLogger().info("Port wurde auf " + port + " gesetzt.");
	}

	public static Settings getInstance()
	{
		if (Objects.isNull(instance))
		{
			instance = new Settings();
		}
		return instance;
	}

	/**
	 * Lädt die Einstellungen.
	 */
	public void loadSettings()
	{
		final Properties toLoad = new Properties();
		try (InputStreamReader input = new InputStreamReader(new FileInputStream(Paths.CONFIG_FILE),
				StandardCharsets.UTF_8);)
		{
			toLoad.load(input);
			int tempPort;
			try
			{
				tempPort = Integer.parseInt(toLoad.getProperty("port", "55001"));
			}
			catch (NumberFormatException e)
			{
				tempPort = 55001;
			}
			setPort(tempPort);

		}
		catch (final Exception e)
		{
			DasChatLogger.getLogger().severe("Konfiguration konnte nicht geladen werden.");
			File configFile = new File(Paths.CONFIG_FILE);
			try
			{
				configFile.createNewFile();
				loadSettings();
			}
			catch (IOException e1)
			{
				DasChatLogger.getLogger().severe("Konfiguration konnte nicht erstellt werden.");
			}
		}
	}

}
