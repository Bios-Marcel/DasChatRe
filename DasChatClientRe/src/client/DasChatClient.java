package client;

import java.util.logging.Level;

import controller.ClientController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logger.DasChatLogger;
import login.DasChatLogin;
import util.DasChatUtil;

public class DasChatClient
{
	private static ClientController controller;

	public static ClientController getController()
	{
		return controller;
	}

	public DasChatClient()
	{
		buildClient();
	}

	private void buildClient()
	{
		Stage primaryStage = new Stage();
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("Client.fxml"));
		controller = new ClientController(primaryStage);
		loader.setController(controller);
		try
		{
			final Parent root = loader.load();
			final Scene scene = new Scene(root);
			primaryStage.setOnCloseRequest(close ->
			{
				Runtime.getRuntime().exit(0);
			});
			primaryStage.setScene(scene);
			primaryStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/cleandaschat.css").toExternalForm());
			primaryStage.setTitle("DasChat");
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/icon.png")));
			primaryStage.show();
			primaryStage.setIconified(false);
			primaryStage.setMaximized(false);
			primaryStage.setMinWidth(primaryStage.getWidth());
			primaryStage.setMinHeight(primaryStage.getHeight());
			new DasChatLogin(primaryStage);
		}
		catch (Exception e)
		{
			DasChatLogger.getLogger().info("DasChat konnte nicht geladen werden.");
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			DasChatUtil.exit();
		}
	}
}
