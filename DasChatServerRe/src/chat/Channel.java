package chat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;

import constants.Paths;
import logger.DasChatLogger;

public class Channel {
	/**
	 * Set welches die Channel Objekte beinhaltet, da jeder Channel nur einmal
	 * existieren kann wurde ein Set genutzt.
	 */
	static Set<Channel> channelSet = new HashSet<>();

	/**
	 * Name des Channels.
	 */
	private String name;

	/**
	 * Set aller für den Channel zugelassen Administratoren
	 */
	private Set<String> admins = new HashSet<>();
	/**
	 * Set welches alle für den Channel zugelassen Nutzer enthält, nicht
	 * relevant fals Channel public ist
	 */
	private Set<String> users = new HashSet<>();

	private boolean publicChannel;

	private List<String> messageSaveQueue = new ArrayList<>();

	private BufferedWriter bufferedWriter;

	public static Set<Channel> getChannels() {
		return channelSet;
	}

	/**
	 * @return @link {@link #publicChannel}
	 */
	public boolean isPublic() {
		return publicChannel;
	}

	/**
	 * @return @link {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return {@link #users}
	 */
	public Set<String> getUsers() {
		return users;
	}

	public Set<String> getAdmins() {
		return admins;
	}

	/**
	 * Gibt für den Nutzer relevante Daten des Channels in Form eines Strings
	 * zurück.
	 */
	@Override
	public String toString() {
		String channelDataAsString = name + "|";
		for (String admin : admins) {
			channelDataAsString = channelDataAsString + "," + admin;
		}
		channelDataAsString = channelDataAsString + "|";
		for (String user : users) {
			channelDataAsString = channelDataAsString + "," + user;
		}
		channelDataAsString = channelDataAsString.replace("|,", "|");
		return channelDataAsString;
	}

	/**
	 * Initialisiert den Channel.
	 * 
	 * @param channelProperties
	 *            Properties des Channel
	 */
	public Channel(File parentDirectory, Properties channelProperties) {
		name = parentDirectory.getName();

		loadChannelData(channelProperties);

		initializeMessageQueueWriter();
	}

	private void initializeMessageQueueWriter() {
		File chatFile = new File(Paths.CHATS_LOCATION + getName() + File.separator + "messages.data");
		System.out.println(chatFile.getAbsolutePath());
		if (!chatFile.exists()) {
			try {
				chatFile.createNewFile();
			} catch (IOException e) {
				DasChatLogger.getLogger().log(Level.SEVERE, "Chat File '" + chatFile.getName()
						+ "' existiert nicht und konnte nicht erstellt werden. (" + e.getMessage() + ")");
			}
		}
		try {
			bufferedWriter = new BufferedWriter(
					new OutputStreamWriter(new FileOutputStream(chatFile, true), StandardCharsets.UTF_8));
		} catch (FileNotFoundException e) {
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	private void loadChannelData(Properties channelProperties) {
		String[] tempUsers = channelProperties.getProperty("users", "").split("[,]");
		if (tempUsers.length != 0) {
			users.addAll(Arrays.asList(tempUsers));
		}
		String[] tempAdmins = channelProperties.getProperty("admins", "").split("[,]");
		if (tempAdmins.length != 0) {
			admins.addAll(Arrays.asList(tempAdmins));
		}
		publicChannel = Boolean.parseBoolean(channelProperties.getProperty("public", "false"));
	}

	/**
	 * Fügt die Nachrichten zur Queue hinzu.
	 * 
	 * @param message
	 *            zu speichernde Nachricht
	 */
	public void addMessageToQueue(String message) {
		messageSaveQueue.add(message);
		saveMessagesIfNeccessary();
	}

	/**
	 * Sobald 20 oder mehr Nachrichten in der Queue sind werden diese
	 * abgespeichert.
	 */
	private void saveMessagesIfNeccessary() {
		if (messageSaveQueue.size() >= 20) {
			for (String message : messageSaveQueue) {
				try {
					bufferedWriter.newLine();
					bufferedWriter.write(message);
				} catch (IOException e) {
					DasChatLogger.getLogger().log(Level.SEVERE,
							"Nachricht konnte nicht gespeichert werden: " + message);
				}
			}
			try {
				bufferedWriter.flush();
				messageSaveQueue.clear();
			} catch (IOException e) {
				DasChatLogger.getLogger().log(Level.SEVERE, "Nachrichten konnten nicht gespeichert werden.");
			}
		}
	}

	/**
	 * Fügt einen Channel zum @link {@link #channelSet Channel Set} hinzu.
	 * 
	 * @param channelToAdd
	 *            hinzuzufügender Channel
	 */
	public static void addChannel(Channel channelToAdd) {
		channelSet.add(channelToAdd);
	}

	/**
	 * Entfernt einen Channel aus @link {@link #channelSet Channel Set}.
	 * 
	 * @param channelToAdd
	 *            zu entfernender Channel
	 */
	public static void removeChannel(Channel channelToRemove) {
		channelSet.remove(channelToRemove);
	}
}
