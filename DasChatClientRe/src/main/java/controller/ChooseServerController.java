package controller;

import java.io.IOException;

import communication.Communication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import login.DasChatClient;
import util.DasChatUtil;

public class ChooseServerController
{

	private Stage				chooseServerStage;

	@FXML
	private ComboBox<String>	chooseServerComboBox;

	@FXML
	private Button				sendButton;

	public ChooseServerController(Stage stage)
	{
		chooseServerStage = stage;
	}

	public void initialize()
	{
		chooseServerComboBox.requestFocus();
	}

	@FXML
	private void connectButtonClicked()
	{
		connectToServer();
	}

	@FXML
	private void enterTyped(KeyEvent e)
	{
		if (e.getCode() == KeyCode.ENTER)
		{
			connectToServer();
		}
	}

	private void connectToServer()
	{
		try
		{
			String ip = chooseServerComboBox.getValue().split("[:]")[0];
			try
			{
				int port = Integer.parseInt(chooseServerComboBox.getValue().split("[:]")[1]);
				Communication.setHost(ip);
				Communication.setPort(port);
				try
				{
					Communication.initalizeCommunicationLayer();
					Communication.sendOwnPublicKey(Communication.getOwnPublicKey().getEncoded());
					byte[] publicKeyBytes = Communication.receiveServerPublicKey();
					Communication.setServerPublicKey(publicKeyBytes);
					chooseServerStage.close();
					new DasChatClient();
				}
				catch (IOException cantConnect)
				{
					chooseServerComboBox.getStyleClass().add("textFieldError");
					DasChatUtil.showErrorDialog("Fehler beim Verbindungsaufbau",
							"Der Server konnte nicht erreicht werden",
							"Stelle bitte sicher das der Server mit dem du dich versuchst zu verbinden online ist und du eine bestehende Verbindung mit dem Internet hast.");
				}
			}
			catch (NumberFormatException invalidPort)
			{
				chooseServerComboBox.getStyleClass().add("textFieldError");
				DasChatUtil.showErrorDialog("Fehler beim Verbindungsaufbau", "Ungültiger Port",
						"Bitte gib einen gültigen Port an");
			}
		}
		catch (ArrayIndexOutOfBoundsException noPort)
		{
			chooseServerComboBox.getStyleClass().add("textFieldError");
			DasChatUtil.showErrorDialog("Fehler beim Verbindungsaufbau", "Kein Port angegeben",
					"Bitte gib einen Port an.");
		}
	}

	@FXML
	private void exitDasChat()
	{
		DasChatUtil.exit();
	}
}
