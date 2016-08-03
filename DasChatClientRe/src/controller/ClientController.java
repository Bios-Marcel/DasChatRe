package controller;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ClientController
{
	Stage				clientStage;

	@FXML
	private Button		sendButton;

	@FXML
	private TextArea	messageTextArea;

	public ClientController(Stage stage)
	{
		clientStage = stage;
	}

	/**
	 * Setzt die Größe der TextArea bei User Eingabe
	 */
	@FXML
	private void onChanged()
	{
		Text text = (Text) messageTextArea.lookup(".text");
		messageTextArea.setPrefHeight(((Bounds) text.boundsInParentProperty().get()).getMaxY() + 25.0);
	}

	/**
	 * Setzt alle Nötigen Dinge für die Komponenten
	 */
	public void initComponents()
	{
		ImageView buttonImage = new ImageView(new Image(this.getClass().getResourceAsStream("/images/sendButton.png")));
		sendButton.setGraphic(buttonImage);

		// NOTE(msc) Setzt die Größe der TextArea initial richtig
		Text text = (Text) messageTextArea.lookup(".text");
		messageTextArea.setPrefHeight(((Bounds) text.boundsInParentProperty().get()).getMaxY() + 25.0);
	}
}
