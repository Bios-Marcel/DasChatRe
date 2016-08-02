package application;

import java.io.File;

import chooseServer.DasChatChooseServer;
import constants.Paths;
import logger.DasChatLogger;

public class DasChatInit
{
	public void init(String[] args)
	{
		new DasChatLogger();

		checkDependencies();

		new DasChatChooseServer().run(args);
	}

	/**
	 * Checks if the main folder exists.
	 */
	private static void checkDependencies()
	{
		File mainFolder = new File(Paths.MAIN_FOLDER);
		if (!mainFolder.exists())
		{
			DasChatLogger.getLogger().warning("Mainfolder doesn't exist.");
			if (mainFolder.mkdir())
			{
				DasChatLogger.getLogger().info("Mainfolder has been created successfully.");
			}
			else
			{
				DasChatLogger.getLogger().severe("Mainfolder couldn't be created.");
			}
		}
	}
}
