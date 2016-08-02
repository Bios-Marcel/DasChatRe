package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Logger;

import logger.DasChatLogger;
import settings.Settings;

public class DasChatServerMain
{

	static Logger logger = DasChatLogger.getLogger();

	public static void main(String[] args)
	{
		logger.info("DasChatServer wurde gestartet");

		Settings.getInstance().loadSettings();

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
	}
}
