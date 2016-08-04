package accounts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;

import logger.DasChatLogger;

public class Account
{
	private static HashMap<String, Account> accounts = new HashMap<>();

	public static void createInstance(String accName, String securitySalt)
	{
		if (!accounts.containsKey(accName))
		{
			accounts.put(accName, new Account(accName, securitySalt));
		}
	}

	public static Account getInstanceForUser(String accName)
	{
		return accounts.get(accName);
	}

	private String	accountName;
	private String	salt;
	private String	connectionSalt;
	private String	password;

	public Account(String accName, String securitySalt)
	{
		connectionSalt = securitySalt;
		loadAccount(accName);
	}

	private void loadAccount(String accName)
	{
		Properties accountProperties = new Properties();
		File accountFile = new File("accounts" + File.separator + accName + ".acc");
		if (accountFile.exists())
		{
			try (FileInputStream input = new FileInputStream(accountFile))
			{
				accountProperties.load(input);
				accountName = accountProperties.getProperty("accountname");
				salt = accountProperties.getProperty("salt");
				password = accountProperties.getProperty("password");

			}
			catch (IOException e)
			{
				DasChatLogger.getLogger().severe("Fehler beim Laden des Benutzers " + accName);
				DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			}
		}
		else
		{
			DasChatLogger.getLogger()
					.severe("Fehler beim Laden des Benutzers " + accName + ", Nutzer existiert nicht.");
			DasChatLogger.getLogger().severe("Datei: " + accountFile.getAbsolutePath().toString());
		}
	}

	public String getPassword()
	{
		return password;
	}

	public String getSalt()
	{
		return salt;
	}

	public String getConnectionSalt()
	{
		return connectionSalt;
	}

}
