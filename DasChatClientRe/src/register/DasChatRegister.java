package register;

import java.util.logging.Level;

import controller.LoginController;
import controller.RegisterController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import logger.DasChatLogger;
import util.DasChatUtil;

public class DasChatRegister
{
	public DasChatRegister()
	{
		buildRegisterDialog();
	}

	private void buildRegisterDialog()
	{
		final Stage stage = new Stage();
		final FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("RegisterDialog.fxml"));
		loader.setController(new RegisterController(stage));
		try
		{
			stage.setTitle("DasChat - Account erstellen");
			stage.initOwner(LoginController.getStage());
			stage.initModality(Modality.WINDOW_MODAL);

			final Parent root = loader.load();
			final Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.getScene().getStylesheets().add(getClass().getResource("/style/application.css").toExternalForm());
			stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/images/icon.png")));
			stage.show();
			stage.setIconified(false);
			stage.setMaximized(false);
			stage.setMinWidth(stage.getWidth());
			stage.setMinHeight(stage.getHeight());
			stage.setMaxWidth(stage.getWidth());
			stage.setMaxHeight(stage.getHeight());
		}
		catch (Exception e)
		{
			DasChatLogger.getLogger().info("DasChat - Account erstellen konnte nicht geladen werden.");
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
			DasChatUtil.exit();
		}
	}

}
