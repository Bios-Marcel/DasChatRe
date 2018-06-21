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
	/**
	 * Beinhaltet alle geladenen Account Objekte.
	 */
	private static HashMap<String, Account> accounts = new HashMap<>();

	/**
	 * Gibt die zum angegebenen User gehörige Account Objektinstanz zurück
	 * 
	 * @param accName
	 *            User dessen Instanz benötigt wird
	 * @return Objektinstanz von Account
	 */
	public static Account getInstanceForUser(String accName)
	{
		return accounts.get(accName);
	}

	/**
	 * Name des Nutzers
	 */
	private String	accountName;

	/**
	 * Salt welcher verwendet wird um "das Passwort sicherer zu machen".
	 */
	private String	salt;

	/**
	 * Vorerst nicht genutzt
	 */
	private String	connectionSalt;

	/**
	 * Passwort des Nutzers (verschlüsselt)
	 */
	private String	password;

	/**
	 * Lädt den Account und setzt den @link {@link #connectionSalt Security
	 * Salt}.
	 * 
	 * @param accName
	 *            zu ladender Account
	 * @param securitySalt
	 *            Salt
	 */
	public Account(String accName, String securitySalt)
	{
		connectionSalt = securitySalt;
		loadAccount(accName);
		accounts.put(accName, this);
	}

	/**
	 * Initialisiert den Account des angegebenen Users.
	 * 
	 * @param accName
	 *            zu ladender Account
	 */
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

	public String getName()
	{
		return accountName;
	}

}
