package org.bnb.athena.utils;

import java.util.Random;

public class StringUtils {

	public static String escape(String x, boolean escapeDoubleQuotes) {
		StringBuilder sBuilder = new StringBuilder(x.length() * 11 / 10);

		int stringLength = x.length();

		for (int i = 0; i < stringLength; ++i) {
			char c = x.charAt(i);

			switch (c) {
			case 0:
				sBuilder.append('\\');
				sBuilder.append('0');

				break;

			case '\n':
				sBuilder.append('\\');
				sBuilder.append('n');

				break;

			case '\r':
				sBuilder.append('\\');
				sBuilder.append('r');

				break;

			case '\\':
				sBuilder.append('\\');
				sBuilder.append('\\');

				break;

			case '\'':
				sBuilder.append('\\');
				sBuilder.append('\'');

				break;

			case '"':
				if (escapeDoubleQuotes) {
					sBuilder.append('\\');
				}

				sBuilder.append('"');

				break;

			case '\032':
				sBuilder.append('\\');
				sBuilder.append('Z');

				break;

			case '\u00a5':
			case '\u20a9':

			default:
				sBuilder.append(c);
			}
		}

		return sBuilder.toString();
	}
	
	public static String generateRandomString(Random rand, int length)
	{
		String characters = "abcdefghijlmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    char[] text = new char[length];
	    for (int i = 0; i < length; i++)
	    {
	        text[i] = characters.charAt(rand.nextInt(characters.length()));
	    }
	    return new String(text);
	}
}