package chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.nodes.Element;

import components.ChannelPane;
import javafx.application.Platform;
import javafx.scene.web.WebView;
import login.DasChatClient;

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
	 * Liste aller Nutzer des jeweiligen Channels.
	 */
	private Set<String> users = new HashSet<>();

	private boolean admin = false;

	private ChannelPane channelPane;

	private List<String> messages = new ArrayList<>();

	private static Channel activeChannel;

	public static Channel getActiveChannel() {
		return activeChannel;
	}

	public static void setActiveChannel(Channel channel) {
		activeChannel = channel;
	}

	public void setChannelPane(ChannelPane channelPane) {
		this.channelPane = channelPane;
	}

	public ChannelPane getChannelPane() {
		return channelPane;
	}

	public static Set<Channel> getChannels() {
		return channelSet;
	}

	public void addMessage(String message) {
		messages.add(message);
	}

	public List<String> getMessages() {
		return messages;
	}

	public String getName() {
		return name;
	}

	private void setName(String name) {
		this.name = name;
	}

	public Set<String> getUsers() {
		return users;
	}

	public boolean isAdmin() {
		return admin;
	}

	private void setAdmin(boolean admin) {
		this.admin = admin;
	}

	/**
	 * Initialisiert den Channel.
	 * 
	 * @param channelProperties
	 *            Properties des Channel
	 */
	public Channel(Element channelElement) {

		setName(channelElement.attr("name"));
		List<String> admins = Arrays.asList(channelElement.attr("admins").split("[,]"));
		users.addAll(Arrays.asList(channelElement.attr("users").split("[,]")));
		setAdmin(admins.contains(DasChatClient.getName()));
	}

	public static void loadMessagesForActiveChannel(WebView webView) {
		String content = "";
		for (String message : Channel.getActiveChannel().getMessages()) {
			content = content + message;
		}
		String finalContent = content;
		Platform.runLater(() -> {
			webView.getEngine()
					.loadContent("<html><head><meta charset='UTF-8'></head><body>" + finalContent + "</body></html>");
		});
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
