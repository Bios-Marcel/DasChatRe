package login;

import java.util.logging.Level;

import controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import logger.DasChatLogger;
import util.DasChatUtil;

public class DasChatClient
{
	public static Scene			scene;
	public static FXMLLoader	loader;

	/**
	 * Referenz auf die Login/Client Stage
	 */
	public static Stage			mainStage;

	/**
	 * Benutzername
	 */
	private static String		name;

	/**
	 * {@link #name Benutzername}
	 * 
	 * @param nameToSet
	 *            zu setzender Name
	 */
	public static void setName(String nameToSet)
	{
		name = nameToSet;
	}

	/**
	 * @return {@link #name Benutzername}
	 */
	public static String getName()
	{
		return name;
	}

	public DasChatClient()
	{
		mainStage = new Stage();
		loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("LoginDialog.fxml"));
		loader.setController(new LoginController());
		try
		{
			final Parent root = loader.load();
			scene = new Scene(root);
			mainStage.setScene(scene);
			mainStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/application.css").toExternalForm());
			// clientStage.getScene().getStylesheets()
			// .add(getClass().getResource("/style/daschatre.css").toExternalForm());
			mainStage.setTitle("DasChat - Login");
			mainStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/icon.png")));
			mainStage.show();
			mainStage.setIconified(false);
			mainStage.setMaximized(false);
			mainStage.setMinWidth(760);
			mainStage.setMinHeight(560);
			mainStage.setMaxWidth(760);
			mainStage.setMaxHeight(560);
			Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
			mainStage.setX((primScreenBounds.getWidth() - mainStage.getWidth()) / 2);
			mainStage.setY((primScreenBounds.getHeight() - mainStage.getHeight()) / 2);
		}
		catch (Exception e)
		{
			DasChatLogger.getLogger().info("DasChat - Login konnte nicht geladen werden.");
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			DasChatUtil.exit();
		}
	}
}
