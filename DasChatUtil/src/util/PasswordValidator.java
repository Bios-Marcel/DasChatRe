package util;

import java.util.regex.Pattern;

public class PasswordValidator
{

	private static final Pattern[] passwordRegexes =
	{ Pattern.compile(".*[A-Z].*"), Pattern.compile(".*[a-z].*"), Pattern.compile(".*\\d.*") };

	public static boolean checkPasswordSecurity(String password)
	{
		if (password.length() < 10)
		{
			return false;
		}
		for (int i = 0; i < passwordRegexes.length; i++)
		{
			if (!passwordRegexes[i].matcher(password).matches())
			{
				return false;
			}
		}
		return true;
	}
}
