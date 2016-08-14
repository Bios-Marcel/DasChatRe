package controller;

import java.util.logging.Level;

import chat.Channel;
import client.DasChatClient;
import communication.Communication;
import constants.Keywords;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import logger.DasChatLogger;
import register.DasChatRegister;
import util.DasChatUtil;

public class LoginController
{
	private static Stage	loginStage;
	private static String	userName;

	// FXML Variables
	@FXML
	private TextField		loginUsername;

	@FXML
	private PasswordField	loginPassword;

	@FXML
	private CheckBox		autoLoginCheckBox;

	@SuppressWarnings("unused")
	private String			connectionSalt;

	public LoginController(Stage stage)
	{
		loginStage = stage;
	}

	public static Stage getStage()
	{
		return loginStage;
	}

	public static String getName()
	{
		return userName;
	}

	@FXML
	private void register()
	{
		new DasChatRegister();
	}

	@FXML
	private void login()
	{
		userName = loginUsername.getText();
		String password = String.format("%064x", new java.math.BigInteger(1,
				DasChatUtil.hashPassword(Keywords.EMPTY_STRING, loginPassword.getText(), 50000)));
		Communication.send("login:" + userName + "password:" + password);
		String reply = Communication.receive();
		if (DasChatUtil.beginningEquals(reply, "login_successful:"))
		{
			// if(autoLoginCheckBox.isSelected()) {
			// //TODO: Gehashes Passwort(password) und
			// Benutzername(userName) in Konfig speichern
			// }
			connectionSalt = reply.replace("login_successful:connectionsalt:", "");
			while (true)
			{
				reply = Communication.receive();
				if (reply.equals("nomorechannels"))
				{
					break;
				}
				if (DasChatUtil.beginningEquals(reply, "channel:"))
				{
					reply = reply.replace("channel:", "");
					Channel.addChannel(new Channel(reply));
				}
				else
				{
					DasChatLogger.getLogger().log(Level.SEVERE, "Channel konnten nicht empfangen werden.");
				}
			}
			DasChatClient.getController().init();
			loginStage.close();
		}
		else if (reply.equals("login_failed"))
		{
			loginUsername.getStyleClass().add("textFieldError");
			loginPassword.getStyleClass().add("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Login", "Fehler bei der Anmeldung",
					"Dein Benutzername oder dein Passwort sind falsch.");
		}
		else
		{
			loginUsername.getStyleClass().remove("textFieldError");
			loginPassword.getStyleClass().remove("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Login", "Fehler bei der Anmeldung",
					"Bei der Anmeldung trat ein unbekannter Fehler auf.");
		}
	}

	@FXML
	private void exitDasChat()
	{
		DasChatUtil.exit();
	}
}
