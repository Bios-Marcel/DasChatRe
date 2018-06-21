package chat;

import java.util.Objects;

public class Message
{
	public static boolean isNonEmpty(String string)
	{
		String toCheck = string;
		if (Objects.isNull(string))
		{
			toCheck = "";
		}
		toCheck = toCheck.replaceAll("\\s", "");
		if (toCheck.length() == 0)
		{
			return false;
		}
		return true;
	}
}
