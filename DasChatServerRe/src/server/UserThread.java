package server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import accounts.Account;
import chat.Channel;
import communication.Communication;
import logger.DasChatLogger;
import util.DasChatUtil;

public class UserThread extends Thread
{
	Socket			serverSocket;
	Communication	communication;
	private String	connectionSalt;
	private Account	userAccount;

	public UserThread(Socket socket)
	{
		super("UserThread" + socket.getLocalAddress());
		serverSocket = socket;
	}

	@Override
	public void run()
	{
		communication = new Communication(serverSocket);
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
						byte[] tempPasswordBytes =
								DasChatUtil.hashPassword(tempSalt, usernameAndPassword.split("password:")[1], 50000);
						String tempPassword = String.format("%064x", new java.math.BigInteger(1, tempPasswordBytes));
						createAccount(tempAccountname, tempSalt, tempPassword);
					}
					else if (DasChatUtil.beginningEquals(message, "login:"))
					{
						String usernameAndPassword = message.replace("login:", "");
						String tempAccountName = usernameAndPassword.split("password:")[0];
						String tempPassword = usernameAndPassword.split("password:")[1];
						connectionSalt = DasChatUtil.generateSalt();
						Account account = new Account(tempAccountName, connectionSalt);
						userAccount = account;
						byte[] tempPasswordBytes = DasChatUtil.hashPassword(account.getSalt(), tempPassword, 50000);
						tempPassword = String.format("%064x", new java.math.BigInteger(1, tempPasswordBytes));
						if (tempPassword.equals(account.getPassword()))
						{
							userAccount = account;
							communication.setAccount(userAccount);
							communication.send("login_successful:connectionsalt:" + connectionSalt);
							for (Channel channel : Channel.getChannels())
							{
								if (channel.getUsers().contains(tempAccountName))
								{
									String channelDataAsString = channel.toString();
									communication.send("channel:" + channelDataAsString);
								}
							}
							communication.send("nomorechannels");
						}
						else
						{
							communication.send("login_failed");
						}
					}
					else if (DasChatUtil.beginningEquals(message, "<div"))
					{
						System.out.println(message);
						// Nachricht bestteht aus
						// CHANNELNAME|NACHRICHT(Beinhaltet restliche
						// Informationen im HTML)
						Document doc = Jsoup.parse(message);
						Element msgElement = doc.getElementById("msg");
						String channelName = msgElement.attr("channel");
						GregorianCalendar gregCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
						gregCalendar.setTime(new Date());
						String time =
								gregCalendar.get(Calendar.HOUR)
										+ ":"
										+ gregCalendar.get(Calendar.MINUTE)
										+ ":"
										+ gregCalendar.get(Calendar.SECOND);

						String date =
								gregCalendar.get(Calendar.DAY_OF_MONTH)
										+ "."
										+ gregCalendar.get(Calendar.MONTH)
										+ "."
										+ gregCalendar.get(Calendar.YEAR);
						msgElement.attr("time", time);
						msgElement.attr("date", date);
						msgElement.attr("timeinmillis", "" + gregCalendar.getTimeInMillis());
						message = doc.getElementById("msg").toString();
						for (Communication userCommunication : DasChatServerMain.userCommunicationInstances)
						{
							for (Channel channel : Channel.getChannels())
							{
								if (channel.getName().equals(channelName))
								{
									channel.addMessageToQueue(message);
									if (channel.getUsers().contains(userCommunication.getAccount().getName()))
									{
										userCommunication.send(message);
									}
								}
							}
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
