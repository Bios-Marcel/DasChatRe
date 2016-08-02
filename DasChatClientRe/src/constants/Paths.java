package constants;

import java.io.File;

public class Paths
{
	public static final String	MAIN_FOLDER	= System.getProperty("user.home") + File.separator + ".DasChat"
			+ File.separator;
	public static final String	LOG_FILE	= MAIN_FOLDER + "Log.txt";
	public static final String	CONFIG_FILE	= MAIN_FOLDER + "config.cfg";
}
