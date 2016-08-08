package chooseServer;

import java.util.logging.Level;

import controller.ChooseServerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import logger.DasChatLogger;
import util.DasChatUtil;

public class DasChatChooseServer extends Application
{

	@Override
	public void start(Stage primaryStage)
	{
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("ChooseServerDialog.fxml"));
		loader.setController(new ChooseServerController(primaryStage));
		try
		{
			final Parent root = loader.load();
			final Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/application.css").toExternalForm());
			primaryStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/daschatre.css").toExternalForm());
			primaryStage.setTitle("DasChat - Server auswählen");
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/icon.png")));
			primaryStage.show();
			primaryStage.setIconified(false);
			primaryStage.setMaximized(false);
			primaryStage.setMinWidth(primaryStage.getWidth());
			primaryStage.setMinHeight(primaryStage.getHeight());
			primaryStage.setMaxWidth(primaryStage.getWidth());
			primaryStage.setMaxHeight(primaryStage.getHeight());
		}
		catch (Exception e)
		{
			DasChatLogger.getLogger().info("DasChat - Server auswählen konnte nicht geladen werden.");
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			DasChatUtil.exit();
		}
	}

	public void run(String[] args)
	{
		launch(args);
	}
}
