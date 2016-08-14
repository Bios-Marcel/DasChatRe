package controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import chat.Channel;
import communication.Communication;
import components.ChannelPane;
import constants.Keywords;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import logger.DasChatLogger;
import util.DasChatUtil;
import util.HTMLUtil;

public class ClientController
{
	private Stage		clientStage;

	@FXML
	private WebView		messageBoard;

	@FXML
	private TextArea	messageTextArea;
	private Text		messageTextAreaText;

	@FXML
	private VBox		chatList;

	// Wird noch genutzt
	@SuppressWarnings("unused")
	private Thread		listenToServerThread;

	private String		webViewContent	= "";

	public ClientController(Stage stage)
	{
		clientStage = stage;
	}

	/**
	 * Setzt die {@link #messageTextArea TextArea zum Nachrichten schreiben} auf
	 * die richtige Größe
	 */
	private void updateTextAreaSize()
	{
		// HACK(MSC) Dies ist ein Workaround, wenn ein PromptText gesetzt ist
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

	/**
	 * Sendet eine Nachricht an den Server
	 */
	private void sendMessage()
	{
		String message = messageTextArea.getText();
		messageTextArea.clear();
		message = HTMLUtil.escapeHTML(message);
		message =
				"<msg id='msg' channel='"
						+ Channel.getActiveChannel().getName()
						+ "' user='"
						+ LoginController.getName()
						+ "'>"
						+ message
						+ "</msg>";
		System.out.println(message);
		Communication.send(message);
	}

	/**
	 * Initialisiert alle nötigen Dinge für den Client
	 */
	public void init()
	{
		// NOTE(msc) Auskommentiert da der Senden Button entfernt wurde.
		// ImageView buttonImage = new ImageView(new
		// Image(this.getClass().getResourceAsStream("/images/sendButton.png")));
		// sendButton.setGraphic(buttonImage);

		initMessageBoard();

		messageTextAreaText = (Text) messageTextArea.lookup(".text");

		updateTextAreaSize();

		initChats();

		initListeners();

		listenToServer();
	}

	private void initMessageBoard()
	{
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
					else if (DasChatUtil.beginningEquals(serverMessage, "<msg"))
					{
						final String message = serverMessage;
						System.out.println(message);
						Document doc = Jsoup.parse(message);
						Element msgElement = doc.getElementById("msg");
						String channelName = msgElement.attr("channel");
						// Nachricht bestteht aus
						// CHANNELNAME|NACHRICHT(Beinhaltet restliche
						// Informationen im HTML)
						if (Channel.getActiveChannel().getName().equals(channelName))
						{
							Platform.runLater(() ->
							{
								System.out.println(message);
								messageBoard.getEngine().loadContent(
										"<html><head><meta charset='UTF-8'></head><body>" + message + "</body></html>");
							});
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
		clientStage.getScene().widthProperty().addListener(new ChangeListener<Number>()
		{
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
			{
				updateTextAreaSize();
			}
		});
	}
}
