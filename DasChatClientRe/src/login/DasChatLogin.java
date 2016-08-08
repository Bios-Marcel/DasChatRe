package login;

import java.util.logging.Level;

import controller.LoginController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import logger.DasChatLogger;
import util.DasChatUtil;

public class DasChatLogin
{

	public DasChatLogin(Stage owner)
	{
		buildLoginDialog(owner);
	}

	private void buildLoginDialog(Stage owner)
	{
		Stage primaryStage = new Stage();
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("LoginDialog.fxml"));
		loader.setController(new LoginController(primaryStage));
		try
		{
			final Parent root = loader.load();
			final Scene scene = new Scene(root);
			primaryStage.initOwner(owner);
			primaryStage.initModality(Modality.APPLICATION_MODAL);
			primaryStage.initStyle(StageStyle.UNDECORATED);
			primaryStage.setScene(scene);
			primaryStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/application.css").toExternalForm());
			primaryStage.getScene().getStylesheets()
					.add(getClass().getResource("/style/daschatre.css").toExternalForm());
			primaryStage.setTitle("DasChat - Login");
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
			DasChatLogger.getLogger().info("DasChat - Login konnte nicht geladen werden.");
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			DasChatUtil.exit();
		}
	}
}
