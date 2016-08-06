package controller;

import client.DasChatClient;
import communication.Communication;
import constants.Keywords;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import register.DasChatRegister;
import util.DasChatUtil;

public class LoginController
{
	private static Stage	loginStage;

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

	@FXML
	private void register()
	{
		new DasChatRegister();
	}

	@FXML
	private void login()
	{
		String password = String.format("%064x", new java.math.BigInteger(1,
				DasChatUtil.hashPassword(Keywords.EMPTY_STRING, loginPassword.getText(), 50000)));
		Communication.send("login:" + loginUsername.getText() + "password:" + password);
		String reply = Communication.receive();
		if (DasChatUtil.beginningEquals(reply, "login_successful:"))
		{
			// if(autoLoginCheckBox.isSelected()) {
			// //TODO: Gehashes Passwort(password) und
			// Benutzername(loginUsername.getText()) in Konfig speichern
			// }
			connectionSalt = reply.replace("login_successful:connectionsalt:", "");
			loginStage.close();
			new DasChatClient();
		}
		else if (reply.equals("login_failed"))
		{
			loginUsername.getStyleClass().add("textFieldError");
			loginPassword.getStyleClass().add("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Login", "Fehler bei der Anemldung",
					"Dein Benutzername oder dein Passwort sind falsch.");
		}
		else
		{
			loginUsername.getStyleClass().remove("textFieldError");
			loginPassword.getStyleClass().remove("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Login", "Fehler bei der Anemldung",
					"Bei der Anmeldung trat ein unbekannter Fehler auf.");
		}
	}

	@FXML
	private void exitDasChat()
	{
		DasChatUtil.exit();
	}
}
