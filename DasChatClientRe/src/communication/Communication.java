package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

import logger.DasChatLogger;
import util.DasChatUtil;

public class Communication
{
	private static Logger			logger	= DasChatLogger.getLogger();

	/**
	 * Socket über welchen die Hauptkommunikation geregelt wird
	 */
	private static Socket			socket;

	/**
	 * Stream über welchen Nachrichten gesendet werden
	 */
	private static DataOutputStream	outputStream;

	/**
	 * Stream über welchen Nachrichten empfangen werden
	 */
	private static DataInputStream	inputStream;

	/**
	 * Addresse des Servers
	 */
	private static String			host;

	/**
	 * Port des Servers
	 */
	private static int				port;

	private static PublicKey		ownPublicKey;
	private static PublicKey		serverPublicKey;
	private static PrivateKey		ownPrivateKey;

	public static void setServerPublicKey(byte[] publicKey)
	{
		try
		{
			serverPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e)
		{
			// TODO(msc) Handle Properly
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	public static PublicKey getOwnPublicKey()
	{
		return ownPublicKey;
	}

	/**
	 * Verbindet sich mit dem Server Socket und setzt In & Output Stream.
	 * 
	 * @throws IOException
	 *             wird geworfen wenn die Verbindung nicht möglich ist
	 */
	public static void initalizeCommunicationLayer() throws IOException
	{

		socket = new Socket(host, port);
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		outputStream = new DataOutputStream(out);
		inputStream = new DataInputStream(in);
		final KeyPair pair = DasChatUtil.generateKey();
		ownPublicKey = pair.getPublic();
		ownPrivateKey = pair.getPrivate();
	}

	/**
	 * Getter für {@link #host}.
	 * 
	 * @return {@link #host}
	 */
	public static String getHost()
	{
		return host;
	}

	public static void setHost(String host)
	{
		Communication.host = host;
	}

	/**
	 * Getter für {@link #port}.
	 * 
	 * @return {@link #port}
	 */
	public static int getPort()
	{
		return port;
	}

	public static void setPort(int port)
	{
		Communication.port = port;
	}

	/**
	 * Sendet einen String als UTF-8 Encoded Byte Array an den Server.
	 *
	 * @param toSend
	 *            String welcher zu senden ist
	 */
	public static void sendOwnPublicKey(byte[] publicKeyBytes)
	{
		try
		{
			outputStream.writeInt(publicKeyBytes.length);
			outputStream.write(publicKeyBytes);
		}
		catch (final IOException e)
		{
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Empfängt ein byte array vom Server , wandelt dieses in einen String um
	 * und gibt ihn zurück
	 *
	 * @return den String der zurückgegeben wird oder null im Falle eines
	 *         Fehlschlages
	 */
	public static byte[] receiveServerPublicKey()
	{
		try
		{
			final int length = inputStream.readInt();
			final byte[] compressed = new byte[length];
			inputStream.readFully(compressed, 0, length);
			return compressed;
		}
		catch (final IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Sendet einen String als UTF-8 Encoded Byte Array an den Server.
	 *
	 * @param toSend
	 *            String welcher zu senden ist
	 */
	public static void send(final String toSend)
	{
		try
		{
			byte[] bytesToSend = toSend.getBytes(StandardCharsets.UTF_8);
			bytesToSend = DasChatUtil.encrypt(bytesToSend, serverPublicKey);
			outputStream.writeInt(bytesToSend.length);
			outputStream.write(bytesToSend);
		}
		catch (final IOException e)
		{
			DasChatLogger.getLogger().log(Level.SEVERE, e.getMessage(), e);
		}
	}

	/**
	 * Empfängt ein byte array vom Server , wandelt dieses in einen String um
	 * und gibt ihn zurück
	 *
	 * @return den String der zurückgegeben wird oder null im Falle eines
	 *         Fehlschlages
	 */
	public static String receive()
	{
		try
		{
			final int length = inputStream.readInt();
			final byte[] compressed = new byte[length];
			inputStream.readFully(compressed, 0, length);
			return new String(DasChatUtil.decrypt(compressed, ownPrivateKey), StandardCharsets.UTF_8);
		}
		catch (final IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
}
