package components;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;

/**
 * 
 * @author marce
 */
public class IconedTextField extends StackPane
{
	private TextField	textField;
	private ImageView	icon;
	private HBox		hbox;

	public IconedTextField()
	{
		super();
		textField = new TextField();
		hbox = new HBox();
		this.icon = new ImageView();
		this.getChildren().add(hbox);
		ObservableList<Node> children = hbox.getChildren();
		children.add(this.icon);
		children.add(textField);

		setStyle("-fx-padding: 10 !important; -fx-background-color: white;");

		hbox.setSpacing(1);
		textField.setStyle("-fx-border-width: 0 !important; -fx-padding: 3 5 3 0 !important;");
		hbox.setHgrow(textField, Priority.ALWAYS);
		hbox.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: darkgrey; -fx-background-color: white;");
	}

	public void setImageView(Image image)
	{
		icon.setImage(image);
	}

	public IconedTextField(ImageView icon)
	{
		super();
		textField = new TextField();
		hbox = new HBox();
		this.icon = icon;
		this.getChildren().add(hbox);
		ObservableList<Node> children = hbox.getChildren();
		children.add(this.icon);
		children.add(textField);

		setStyle("-fx-padding: 10 10 10 10 !important; -fx-background-color: white;");

		hbox.setSpacing(5);
		hbox.setMargin(textField, new Insets(0, 10, 0, 0));
		textField.setStyle("-fx-border-width: 0 !important;");
		hbox.setHgrow(textField, Priority.ALWAYS);
		hbox.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: darkgrey; -fx-background-color: white;");
	}

	public void setPromptText(String promptText)
	{
		textField.setPromptText(promptText);
	}

	public TextField getTextField()
	{
		return textField;
	}
}
