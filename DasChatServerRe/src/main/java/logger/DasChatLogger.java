package logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import constants.Paths;

public class DasChatLogger
{
	private static Logger logger;

	static
	{
		initializeLogger();
	}

	public static Logger getLogger()
	{
		return logger;
	}

	/**
	 * Initialisiert den Logger.
	 */
	private static void initializeLogger()
	{
		try
		{
			logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			logger.setLevel(Level.FINEST);
			final FileHandler logFile = new FileHandler(Paths.LOG_FILE, true);
			final LogFormatter formatterTxt = new LogFormatter();

			logFile.setFormatter(formatterTxt);
			logger.addHandler(logFile);
		}
		catch (final SecurityException | IOException e)
		{
			System.out.println("Der Logger konnte nicht initialisiert werden.");
			System.out.println(e.fillInStackTrace().toString());
		}
	}
}
