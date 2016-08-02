package util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;

public class DasChatUtil
{
	/**
	 * Generates a random salt and returns it as a String
	 * 
	 * @return random generated Salt
	 */
	public static String generateSalt()
	{
		final Random saltGenerator = new SecureRandom();
		byte[] saltBytes = new byte[32];
		saltGenerator.nextBytes(saltBytes);

		String saltString = String.format("%064x", new java.math.BigInteger(1, saltBytes));
		return saltString;
	}

	public static void showErrorDialog(String title, String headerText, String contentText)
	{
		Alert errorAlert = new Alert(AlertType.ERROR);
		errorAlert.setTitle(title);
		errorAlert.setHeaderText(headerText);
		errorAlert.setContentText(contentText);
		errorAlert.initModality(Modality.APPLICATION_MODAL);
		errorAlert.show();
	}

	/**
	 * Checks if the start of the src String equals the String toSearchFor
	 * 
	 * @param src
	 *            Sourche
	 * @param toSearchFor
	 *            String to search for in source
	 * @return true if it equals
	 */
	public static boolean beginningEquals(String src, String toSearchFor)
	{
		if (src.length() >= toSearchFor.length())
		{
			if (src.substring(0, toSearchFor.length()).equals(toSearchFor))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Hashes a given password 10 Million times using a salt and SHA-256
	 * 
	 * @param password
	 *            password that is to hash
	 * @return hashed password as bytes
	 * @throws NoSuchAlgorithmException
	 *             if a wrong algorithm is used
	 */
	public static byte[] hashPassword(String saltString, String password, int rounds)
	{
		byte[] hash = null;
		try
		{
			String toHash = saltString + password;

			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(toHash.getBytes(StandardCharsets.UTF_8));

			for (int i = 0; i < rounds; i++)
			{
				hash = digest.digest(hash);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hash;
	}

	/**
	 * Hashes a given password 10 Million times using a salt and SHA-256
	 * 
	 * @param password
	 *            password that is to hash
	 * @return hashed password as bytes
	 * @throws NoSuchAlgorithmException
	 *             if a wrong algorithm is used
	 */
	public static byte[] hashPassword(String password, int rounds)
	{
		byte[] hash = null;
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

			for (int i = 0; i < rounds; i++)
			{
				hash = digest.digest(hash);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hash;
	}

	/**
	 * Compresses and returns a given byte array.
	 * 
	 * @param data
	 *            data that is to compress
	 * @return compressed data or null if failed
	 */
	public static byte[] compress(byte[] data)
	{
		Deflater deflater = new Deflater();
		deflater.setInput(data);

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);)
		{
			deflater.finish();
			byte[] buffer = new byte[1024];
			while (!deflater.finished())
			{
				int count = deflater.deflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			deflater.end();
			byte[] output = outputStream.toByteArray();
			return output;
		}
		catch (Exception e)
		{
			return null;
		}

	}

	/**
	 * Decompresses and returns a given byte array.
	 * 
	 * @param data
	 *            data that is to decompress
	 * @return decompressed data or null if failed
	 */
	public static byte[] decompress(byte[] data)
	{
		Inflater inflater = new Inflater();
		inflater.setInput(data);

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);)
		{
			byte[] buffer = new byte[1024];
			while (!inflater.finished())
			{
				int count = inflater.inflate(buffer);
				outputStream.write(buffer, 0, count);
			}
			inflater.end();
			byte[] output = outputStream.toByteArray();
			return output;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static void exit()
	{
		Runtime.getRuntime().exit(0);
	}
}
