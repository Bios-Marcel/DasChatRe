package components;

import chat.Channel;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class ChannelPane extends HBox
{
	private Label		channelName;
	private ImageView	icon;
	private StackPane	centerChannelName;
	private StackPane	centerIcon;

	public ChannelPane(String name)
	{
		channelName = new Label(name);
		centerChannelName = new StackPane(channelName);

		getStyleClass().add("chatPane");
		// getChildren().add(centerIcon);
		getChildren().add(centerChannelName);

		initListeners();
	}

	private void initListeners()
	{
		setOnMouseClicked(clicked ->
		{
			for (Channel channel : Channel.getChannels())
			{
				channel.getChannelPane().setStyle("-fx-background-color: white !important");
				if (this.equals(channel.getChannelPane()))
				{
					Channel.setActiveChannel(channel);
				}
			}
			setStyle("-fx-background-color: -fx-focus-color !important;");
		});
	}
}
