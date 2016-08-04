package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;

import accounts.Account;
import communication.Communication;
import logger.DasChatLogger;
import util.DasChatUtil;

public class UserThread extends Thread
{
	Socket			serverSocket;
	Communication	communication;
	private String	connectionSalt;

	public UserThread(Socket socket)
	{
		serverSocket = socket;
		communication = new Communication(socket);
		try
		{
			communication.initalizeCommunicationLayer();
		}
		catch (IOException e)
		{
			disconnectUser();
		}
		listenToIncommingCommunication();
	}

	private void disconnectUser()
	{
		try
		{
			serverSocket.close();
			this.interrupt();
		}
		catch (IOException e1)
		{
			e1.printStackTrace();
		}
	}

	private void listenToIncommingCommunication()
	{
		DasChatLogger.getLogger().info("Listening to Socket: " + serverSocket.getInetAddress());
		try
		{
			byte[] publicKeyBytes = communication.receivePublicKey();
			communication.setClientPublicKey(publicKeyBytes);
			communication.sendOwnPublicKey(communication.getOwnPublicKey().getEncoded());
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
		while (true)
		{
			try
			{
				String message = communication.receive();
				if (!Objects.isNull(message))
				{
					if (DasChatUtil.beginningEquals(message, "register:"))
					{
						String usernameAndPassword = message.replace("register:", "");
						String tempAccountname = usernameAndPassword.split("password:")[0];
						String tempSalt = DasChatUtil.generateSalt();
						byte[] tempPasswordBytes = DasChatUtil.hashPassword(tempSalt,
								usernameAndPassword.split("password:")[1], 50000);
						String tempPassword = String.format("%064x", new java.math.BigInteger(1, tempPasswordBytes));
						createAccount(tempAccountname, tempSalt, tempPassword);
					}
					else if (DasChatUtil.beginningEquals(message, "login:"))
					{
						String usernameAndPassword = message.replace("login:", "");
						String tempAccountName = usernameAndPassword.split("password:")[0];
						String tempPassword = usernameAndPassword.split("password:")[1];
						connectionSalt = DasChatUtil.generateSalt();
						Account.createInstance(tempAccountName, connectionSalt);
						Account account = Account.getInstanceForUser(tempAccountName);
						byte[] tempPasswordBytes = DasChatUtil.hashPassword(account.getSalt(), tempPassword, 50000);
						tempPassword = String.format("%064x", new java.math.BigInteger(1, tempPasswordBytes));
						if (tempPassword.equals(account.getPassword()))
						{
							communication.send("login_successful:connectionsalt:" + connectionSalt);
						}
						else
						{
							communication.send("login_failed");
						}
					}
				}
			}
			catch (IOException e)
			{
				try
				{
					serverSocket.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
				this.interrupt();
				break;
			}
		}
	}

	private void createAccount(String accName, String salt, String password)
	{
		File accountFile = new File("accounts" + File.separator + accName + ".acc");
		if (!accountFile.exists())
		{
			try
			{
				accountFile.createNewFile();
				try (FileOutputStream output = new FileOutputStream(accountFile))
				{
					Properties properties = new Properties();
					properties.setProperty("accountname", accName);
					properties.setProperty("salt", salt);
					properties.setProperty("password", password);
					properties.store(new FileOutputStream(accountFile), null);
					communication.send("registration_successful");
				}
				catch (IOException e)
				{
					DasChatLogger.getLogger().severe("Benutzer konnte nicht angelegt werden.");
					DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
					communication.send("registration_failed");
				}
			}
			catch (IOException e)
			{
				DasChatLogger.getLogger().severe("Benutzer konnte nicht angelegt werden.");
				DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
				communication.send("registration_failed");
			}
		}
		else
		{
			DasChatLogger.getLogger().severe("Benutzer konnte nicht angelegt werden.");
			communication.send("username_taken");
		}
	}

}
