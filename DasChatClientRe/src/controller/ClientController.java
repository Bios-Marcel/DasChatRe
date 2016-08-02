package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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

	public void initComponents()
	{
		ImageView buttonImage = new ImageView(new Image(this.getClass().getResourceAsStream("/images/sendButton.png")));
		sendButton.setGraphic(buttonImage);
		Text text = (Text) messageTextArea.lookup(".text");
		messageTextArea.addEventHandler(KeyEvent.KEY_RELEASED, keyHandler ->
		{
			double height = text.boundsInParentProperty().get().getMaxY() + 25;
			System.out.println(height);
			messageTextArea.setPrefHeight(height);
		});
	}
}
