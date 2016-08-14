package util;

public class HTMLUtil
{

	/**
	 * Escape characters that are special in HTML, so that the resulting string
	 * can be included in HTML (or XML). For instance, this method will convert
	 * an embedded "&amp;" to "&amp;amp;".
	 *
	 * @param s
	 *            the string to convert
	 *
	 * @return the converted string
	 */
	public static String escapeHTML(String s)
	{
		String escapedHTML = s;
		escapedHTML = escapedHTML.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
		escapedHTML = escapedHTML.replace("\n", "<br/>").replace(" ", "&nbsp;").replace("\t", "&#9;");
		return escapedHTML;
	}

}
