package controller;

import communication.Communication;
import constants.Keywords;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import logger.DasChatLogger;

public class ClientController
{
	private Stage		clientStage;

	@FXML
	private TextArea	messageTextArea;
	private Text		messageTextAreaText;

	private Thread		listenToServerThread;

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
			sendMessage();
		}
	}

	private void sendMessage()
	{
		// TODO(msc) Code
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

		messageTextAreaText = (Text) messageTextArea.lookup(".text");

		updateTextAreaSize();

		initListeners();

		listenToServer();
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
					// TODO(msc) handle server response
					if (serverMessage.equals(Keywords.ERROR_WHILE_RECEIVING_MESSAGE))
					{
						DasChatLogger.getLogger().severe("Die Verbindung mit dem Server wurde unterbrochen.");
					}
				}

			}
		};
		listenToServerThread = new Thread(listening);

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
