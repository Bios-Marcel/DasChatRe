package communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import logger.DasChatLogger;
import util.DasChatUtil;

public class Communication
{
	private Socket				socket;

	private DataOutputStream	outputStream;

	private DataInputStream		inputStream;

	private String				host;

	private int					port;

	public Communication(Socket inputSocket)
	{
		socket = inputSocket;
	}

	public void initalizeCommunicationLayer() throws IOException
	{
		OutputStream out = socket.getOutputStream();
		InputStream in = socket.getInputStream();
		outputStream = new DataOutputStream(out);
		inputStream = new DataInputStream(in);
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String inputHost)
	{
		host = inputHost;
	}

	public int getPort()
	{
		return port;
	}

	public void setPort(int inputPort)
	{
		port = inputPort;
	}

	/**
	 * Sendet einen String als UTF-8 Encoded Byte Array an den Server.
	 *
	 * @param toSend
	 *            String welcher zu senden ist
	 */
	public void send(final String toSend)
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
	public String receive() throws IOException
	{
		final int length = inputStream.readInt();
		final byte[] compressed = new byte[length];
		inputStream.readFully(compressed, 0, length);
		final byte[] message = DasChatUtil.decompress(compressed);
		return new String(message, StandardCharsets.UTF_8);
	}
}
