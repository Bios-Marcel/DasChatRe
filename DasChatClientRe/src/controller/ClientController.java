package controller;

import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import chat.Channel;
import chat.Message;
import communication.Communication;
import components.ChannelPane;
import components.IconedTextField;
import constants.Keywords;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import logger.DasChatLogger;
import login.DasChatClient;
import util.DasChatUtil;
import util.HTMLUtil;

public class ClientController
{
	@FXML
	private WebView			messageBoard;

	@FXML
	private TextArea		messageTextArea;
	private Text			messageTextAreaText;

	// @FXML
	// private ImageView sendButton;

	@FXML
	private IconedTextField	searchBar;

	@FXML
	private VBox			chatList;

	@FXML
	private ImageView		sendButton;

	@FXML
	private BorderPane		buttonContainer;

	@FXML
	private ImageView		plusButton;

	@FXML
	private BorderPane		plusContainer;

	private Thread			listenToServerThread;

	private Thread			initThread;

	public WebView getMessageBoard()
	{
		return messageBoard;
	}

	/**
	 * Setzt die {@link #messageTextArea TextArea zum Nachrichten schreiben} auf
	 * die richtige Größe
	 */
	private void updateTextAreaSize()
	{
		messageTextAreaText = (Text) messageTextArea.lookup(".text");
		// HACK(MSC) Dies ist ein Workaround, wenn ein PromptText gesetzt
		// ist
		// kann die TextArea ihre Größe nicht ändern.
		if (messageTextArea.getText().length() == 0)
		{
			messageTextArea.setPromptText("Gib eine Nachricht ein ...");
		}
		else
		{
			messageTextArea.setPromptText(null);
		}
		messageTextArea.setPrefHeight(messageTextAreaText.boundsInParentProperty().get().getMaxY() + 30.0);
	}

	/**
	 * Behandelt die Tastendrücke in der {@link #messageTextArea TextArea zum
	 * Nachrichten schreiben}.
	 * 
	 * @param e
	 *            vom FXML übergebenes KeyEvent
	 */
	@FXML
	private void messageTextAreaKeyListener(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
		{
			if (e.isShiftDown())
			{
				messageTextArea.insertText(messageTextArea.getCaretPosition(), System.lineSeparator());
			}
			else
			{
				e.consume();
				sendMessage();
			}
		}
	}

	@FXML
	private void sendButtonClicked(MouseEvent e)
	{
		sendMessage();
	}

	/**
	 * Sendet eine Nachricht an den Server
	 */
	private void sendMessage()
	{
		if (!Objects.isNull(Channel.getActiveChannel()))
		{
			String message = messageTextArea.getText();
			if (Message.isNonEmpty(message))
			{
				messageTextArea.clear();
				message = HTMLUtil.escapeHTML(message);
				message =
						"<div id='msg' channel='"
								+ Channel.getActiveChannel().getName()
								+ "' user='"
								+ DasChatClient.getName()
								+ "'>"
								+ message
								+ "</div>";
				Communication.send(message);
			}
		}
	}

	/**
	 * Initialisiert alle nötigen Dinge für den Client
	 */
	public void init()
	{
		initMessageBoard();

		initChats();

		initListeners();

		searchBar.setImageView(new Image(getClass().getResource("/images/searchsmall.png").toExternalForm()));
		searchBar.setPromptText("Search");

		listenToServer();

		sendButton.setImage(new Image(getClass().getResource("/images/sendButton.png").toExternalForm()));
		plusButton.setImage(new Image(getClass().getResource("/images/plus.png").toExternalForm()));
	}

	private void initMessageBoard()
	{
		messageBoard.getEngine()
				.setUserStyleSheetLocation(getClass().getResource("/chatstyle/default.css").toExternalForm());
		String landingPage =
				"<html><div style='text-align:center; width: 98%; position: absolute; top: 50%; translateY(-50%);'><span style='color: lightgrey; fonz-size: 20px;'>Wähle einen Chat/Chat Partner aus.</span></div></html>";
		messageBoard.getEngine().loadContent(landingPage);
	}

	private void initChats()
	{
		for (Channel channel : Channel.getChannels())
		{
			ChannelPane channelPane = new ChannelPane(channel.getName());
			channel.setChannelPane(channelPane);
			chatList.getChildren().add(channelPane);
		}
	}

	private void listenToServer()
	{
		Runnable listening = new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					String serverMessage = Communication.receive();
					if (serverMessage.equals(Keywords.ERROR_WHILE_RECEIVING_MESSAGE))
					{
						DasChatLogger.getLogger().severe("Die Verbindung mit dem Server wurde unterbrochen.");
					}
					else if (DasChatUtil.beginningEquals(serverMessage, "<div"))
					{
						final String message = serverMessage;
						System.out.println(message);
						Document doc = Jsoup.parse(message);
						Element msgElement = doc.getElementById("msg");
						String channelName = msgElement.attr("channel");
						String sender = msgElement.attr("user");
						if (sender.equals(DasChatClient.getName()))
						{
							msgElement.attr("class", "messageblobme");
						}
						else
						{
							msgElement.attr("class", "messageblob");
						}
						String finalMessage = doc.getElementById("msg").toString();
						// Nachricht bestteht aus
						// CHANNELNAME|NACHRICHT(Beinhaltet restliche
						// Informationen im HTML)
						for (Channel channel : Channel.getChannels())
						{
							if (channel.getName().equals(channelName))
							{
								channel.addMessage(finalMessage);
							}
						}
						if (Channel.getActiveChannel().getName().equals(channelName))
						{
							Channel.loadMessagesForActiveChannel(messageBoard);
						}
					}
				}

			}
		};

		listenToServerThread = new Thread(listening);
		listenToServerThread.start();
	}

	/**
	 * Initialisiert die nötigen Listener für den Client.
	 */
	private void initListeners()
	{
		messageTextArea.textProperty().addListener(new ChangeListener<String>()
		{
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
			{
				updateTextAreaSize();
			}
		});
	}
}
