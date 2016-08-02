package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

import logger.DasChatLogger;
import util.DasChatUtil;

public class Communication
{
	static Logger					logger	= DasChatLogger.getLogger();

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
	public static void send(final String toSend)
	{
		try
		{
			byte[] bytesToSend = toSend.getBytes(StandardCharsets.UTF_8);
			bytesToSend = DasChatUtil.compress(bytesToSend);
			outputStream.writeInt(bytesToSend.length);
			outputStream.write(bytesToSend);
		}
		catch (final IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
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
			final byte[] message = DasChatUtil.decompress(compressed);
			return new String(message, StandardCharsets.UTF_8);
		}
		catch (final IOException e)
		{
			logger.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}
}
