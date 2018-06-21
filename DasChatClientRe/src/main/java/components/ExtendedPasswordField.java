package components;

import javafx.scene.control.PasswordField;
import javafx.scene.input.Clipboard;

/**
 * Vehindert das der Nutzer sein Passwort einfach kopiert und somit vielleicht
 * selbst nicht weiß was er eingibt.
 * 
 * @author Marcel Schramm
 *
 */
public class ExtendedPasswordField extends PasswordField
{
	@Override
	public void paste()
	{
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasString())
		{
			clipboard.clear();
		}
	}
}
