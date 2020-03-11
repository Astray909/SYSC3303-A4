/**
 * 
 */
package pkj;

import java.net.*;

/**
 * @author Jia Chen Huang
 * @version Feb 3, 2020
 *
 */
public class IntHost
{
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	static final String INTHOST = "Intermediate Server";
	public static final int port = 23;

	/**
	 * constructor
	 */
	public IntHost()
	{
		try
		{
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(port);
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Sends reply packet from server back to client
	 */
	private void reply()
	{
		DatagramPacket receivePacket = Client.waitPacket(receiveSocket, INTHOST);
		int clientPort = receivePacket.getPort();
		sleepThread(1);
		Client.sendPacket(receivePacket.getData(), receivePacket.getLength(), receivePacket.getAddress(), Server.port, sendSocket, INTHOST);
		DatagramPacket reply = Client.waitPacket(receiveSocket, "Intermediate Server");
		Client.sendPacket(reply.getData(), reply.getLength(), reply.getAddress(), clientPort, sendSocket, INTHOST);
	}

	/**
	 * put the currently running thread to sleep for int seconds
	 * @param seconds: the amount of seconds to sleep
	 */
	private static void sleepThread(int seconds)
	{
		try
		{
			Thread.sleep(seconds*1000);
		}
		catch(InterruptedException ie)
		{
			ie.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		IntHost i = new IntHost();
		while(true) {
			i.reply();
		}
	}

}
