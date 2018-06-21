package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import chat.Channel;
import communication.Communication;
import constants.Paths;
import exceptions.PropertiesLoadException;
import logger.DasChatLogger;
import settings.Settings;
import util.DasChatUtil;

public class DasChatServerMain
{

	static Logger						logger						= DasChatLogger.getLogger();

	public static List<Communication>	userCommunicationInstances	= new ArrayList<>();

	public static void main(String[] args)
	{
		logger.info("DasChatServer wurde gestartet");

		Settings.getInstance().loadSettings();

		initializeChats();

		listenToIncommingConnections();
	}

	/**
	 * Überprüft ob der Chat Ordner existiert und erstellt diesen gegebenfalls.
	 * Anschließend werden alle Chats welche geladen werden können geladen.
	 */
	private static void initializeChats()
	{
		File chatFolder = new File(Paths.CHATS_LOCATION);
		if (chatFolder.exists())
		{
			File[] chatFolders = chatFolder.listFiles();
			for (File file : chatFolders)
			{
				if (file.isDirectory())
				{
					File config = new File(file.getPath() + File.separator + "config.cfg");
					if (config.exists())
					{
						try
						{
							Properties properties = DasChatUtil.loadProperties(config.getPath());

							Channel.addChannel(new Channel(file, properties));

						}
						catch (PropertiesLoadException e)
						{
							logger.log(Level.SEVERE, e.getMessage());
						}
					}
				}
			}
		}
		else
		{
			chatFolder.mkdir();
			initializeChats();
		}
	}

	/**
	 * Akzeptiert einkommende Vrebindungen.
	 */
	private static void listenToIncommingConnections()
	{

		Thread thread = new Thread(() ->
		{
			try (ServerSocket serverSocket = new ServerSocket(Settings.getInstance().getPort()))
			{
				while (true)
				{
					new UserThread(serverSocket.accept()).start();
				}
			}
			catch (final IOException e)
			{
				logger.severe("Port " + Settings.getInstance().getPort() + " ist bereits in Benutzung!");
				Runtime.getRuntime().exit(0);
			}
		});
		thread.start();
	}
}
