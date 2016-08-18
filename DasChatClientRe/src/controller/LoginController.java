package controller;

import java.util.logging.Level;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import chat.Channel;
import communication.Communication;
import constants.Keywords;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logger.DasChatLogger;
import login.DasChatClient;
import register.DasChatRegister;
import util.DasChatUtil;

public class LoginController {
	private String userName;

	// FXML Variables
	@FXML
	private TextField loginUsername;

	@FXML
	private PasswordField loginPassword;

	@FXML
	private CheckBox autoLoginCheckBox;

	@SuppressWarnings("unused")
	private String connectionSalt;

	@FXML
	private void register() {
		new DasChatRegister();
	}

	@FXML
	private void loginButtonClicked() {
		login();
	}

	@FXML
	private void enterPressed(KeyEvent e) {
		if (e.getCode() == KeyCode.ENTER) {
			login();
		}
	}

	private void login() {
		userName = loginUsername.getText();
		String password = String.format("%064x", new java.math.BigInteger(1,
				DasChatUtil.hashPassword(Keywords.EMPTY_STRING, loginPassword.getText(), 50000)));
		Communication.send("login:" + userName + "password:" + password);
		DasChatClient.setName(userName);
		String reply = Communication.receive();
		if (DasChatUtil.beginningEquals(reply, "login_successful:")) {
			// if(autoLoginCheckBox.isSelected()) {
			// //TODO: Gehashes Passwort(password) und
			// Benutzername(userName) in Konfig speichern
			// }
			connectionSalt = reply.replace("login_successful:connectionsalt:", "");
			while (true) {
				reply = Communication.receive();
				if (reply.equals("nomorechannels")) {
					break;
				}

				if (DasChatUtil.beginningEquals(reply, "<data")) {
					Document channelDocument = Jsoup.parse(reply);
					Element channelElement = channelDocument.getElementById("channel");
					System.out.println(channelElement);
					Channel.addChannel(new Channel(channelElement));
				} else {
					DasChatLogger.getLogger().log(Level.SEVERE, "Channel konnten nicht empfangen werden.");
				}
			}
			// DasChatClient.getController().init();
			loadClientScene();
		} else if (reply.equals("login_failed")) {
			loginUsername.getStyleClass().add("textFieldError");
			loginPassword.getStyleClass().add("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Login", "Fehler bei der Anmeldung",
					"Dein Benutzername oder dein Passwort sind falsch.");
		} else {
			loginUsername.getStyleClass().remove("textFieldError");
			loginPassword.getStyleClass().remove("textFieldError");
			DasChatUtil.showErrorDialog("DasChat - Login", "Fehler bei der Anmeldung",
					"Bei der Anmeldung trat ein unbekannter Fehler auf.");
		}
	}

	private void loadClientScene() {
		DasChatClient.loader = new FXMLLoader();
		DasChatClient.loader.setLocation(getClass().getResource("/client/Client.fxml"));
		Stage clientStage = DasChatClient.mainStage;
		ClientController controller = new ClientController();
		DasChatClient.loader.setController(controller);
		try {
			final Parent root = DasChatClient.loader.load();
			DasChatClient.scene = new Scene(root);
			clientStage.setOnCloseRequest(close -> {
				Runtime.getRuntime().exit(0);
			});
			clientStage.setScene(DasChatClient.scene);
			clientStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/cleandaschat.css").toExternalForm());
			clientStage.setTitle("DasChat");
			clientStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/icon.png")));
			clientStage.show();
			clientStage.setIconified(false);
			clientStage.setMaximized(false);
			clientStage.setResizable(true);
			// TODO(msc) auf Double.MAX_VALUE setzen?
			clientStage.setMaxHeight(Screen.getPrimary().getBounds().getHeight());
			clientStage.setMaxWidth(Screen.getPrimary().getBounds().getWidth());
			clientStage.setMinWidth(760);
			clientStage.setMinHeight(560);
			controller.init();
		} catch (Exception e) {
			DasChatLogger.getLogger().info("DasChat konnte nicht geladen werden.");
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			DasChatUtil.exit();
		}
	}

	@FXML
	private void exitDasChat() {
		DasChatUtil.exit();
	}
}
