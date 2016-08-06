package controller;

import communication.Communication;
import components.ExtendedPasswordField;
import constants.Keywords;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import util.DasChatUtil;
import util.PasswordValidator;

public class RegisterController
{
	private Stage					registerStage;

	// FXML Variables
	@FXML
	private TextField				registerAccountname;

	@FXML
	private ExtendedPasswordField	registerPassword;

	@FXML
	private ExtendedPasswordField	registerPasswordAgain;

	public RegisterController(Stage stage)
	{
		registerStage = stage;
	}

	@FXML
	private void submitRegister()
	{
		if (registerPassword.getText().equals(registerPasswordAgain.getText()))
		{
			String password = registerPassword.getText();
			if (PasswordValidator.checkPasswordSecurity(password))
			{
				registerPassword.getStyleClass().remove("textFieldError");
				registerPasswordAgain.getStyleClass().remove("textFieldError");
				String hashedPassword = String.format("%064x", new java.math.BigInteger(1,
						DasChatUtil.hashPassword(Keywords.EMPTY_STRING, registerPassword.getText(), 50000)));
				Communication.send("register:" + registerAccountname.getText() + "password:" + hashedPassword);
				String reply = Communication.receive();
				switch (reply)
				{
					case "registration_successful":
					{
						registerStage.close();
						break;
					}
					case "username_taken":
					{
						registerPassword.getStyleClass().remove("textFieldError");
						registerPasswordAgain.getStyleClass().remove("textFieldError");
						registerAccountname.getStyleClass().add("textFieldError");
						DasChatUtil.showErrorDialog("DasChat - Registrierung", "Benutzername bereits vergeben",
								"Der von dir eingegebene Benutzername ist bereits von einem anderen Benutzer in Verwendung, bitte wähle einen anderen.");
						break;
					}
					default:
					{
						registerPassword.getStyleClass().remove("textFieldError");
						registerPasswordAgain.getStyleClass().remove("textFieldError");
						registerAccountname.getStyleClass().remove("textFieldError");
						DasChatUtil.showErrorDialog("DasChat - Registrierung", "Fehler bei der Registrierung",
								"Bei der Erstellung deines Accounts trat ein unbekannter Fehler auf, bitte versuche es erneut.");
					}
				}
			}
			else
			{
				registerPassword.getStyleClass().add("textFieldError");
				registerPasswordAgain.getStyleClass().add("textFieldError");
				DasChatUtil.showErrorDialog("DasChat - Registrierung", "Ungültiges Passwort",
						"Das von dir eingegebene Passwort ist nicht sicher genug, es muss mindestens einen Großbuchstaben, einen Kleinbuchstaben, eine Zahl und ein Symbol enthalten, außerdem muss es mindestens 10 Zeichen lang sein.");
			}
		}
		else
		{

			registerPassword.getStyleClass().add("textFieldError");
			registerPasswordAgain.getStyleClass().add("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Registrierung", "Passwörter stimmen nicht überein",
					"Die von dir eingegebenen Passwörter stimmen nicht überein.");
		}
	}

	@FXML
	private void backToLogin()
	{
		registerStage.close();
	}
}
