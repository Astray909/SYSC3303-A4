/**
 * 
 */
package pkj;

import java.io.*;
import java.net.*;

/**
 * @author Jia Chen Huang
 * @version Feb 3, 2020
 *
 */
public class Client
{
	private DatagramSocket sendReceiveSocket;

	/**
	 * constructor
	 */
	public Client()
	{
		try {
			sendReceiveSocket = new DatagramSocket();
		} catch (SocketException se) {
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * build a packet that sends to a specific port on a host
	 * @param pkg: the package to be sent
	 * @param port: the port where the poackage will be sent to
	 */
	public void sendAndReceive(byte[] msg, int port)
	{
		try {
			sendPacket(msg, msg.length, InetAddress.getLocalHost(), IntHost.port, sendReceiveSocket, "Client");
		}
		catch (UnknownHostException he){
			he.printStackTrace();
			System.exit(1);
		}
		waitPacket(sendReceiveSocket, "Client");
	}

	/**
	 * merges two arrays
	 * @param a: one of the arrays
	 * @param b: the other array
	 * @return result: the merged array
	 */
	private static byte[] concatenate(byte[] a, byte[] b)
	{
		int aLen = a.length;
		int bLen = b.length;

		byte[] result = new byte[aLen + bLen];

		System.arraycopy(a, 0, result, 0, aLen);
		System.arraycopy(b, 0, result, aLen, bLen);

		return result;
	}

	/**
	 * builds the byte array to be passed as message
	 * @param r: r for read mode, !r for write mode
	 * @param fileName: filename
	 * @param mode: mode, octet or netascii
	 * @return returns the byte array
	 */
	private static byte[] msgBuilder(boolean r, String fileName, String mode)
	{
		byte[] read = {0, 1};
		byte[] write = {0, 2};
		byte[] zero = {0};

		byte[] header = new byte[2];
		if(r)
		{
			header = read;
		}
		else
		{
			header = write;
		}

		if(!mode.equalsIgnoreCase("octet") && !mode.equalsIgnoreCase("netascii"))
		{
			System.out.println("wrong mode, please change mode to either octet or netascii");
			System.exit(1);
		}

		byte[] NAME = fileName.getBytes();
		byte[] MODE = mode.getBytes();

		return makeMsg(header, read, write, zero, NAME, MODE);
	}

	/**
	 * construct byte array
	 * @param header: header bytes, 01 for read, 02 for write
	 * @param read: read or write state
	 * @param write: read or write state
	 * @param zero: to insert zero
	 * @param fileName: 
	 * @param mode
	 * @return
	 */
	private static byte[] makeMsg(byte[] header, byte[] read, byte[] write, byte[] zero, byte[] NAME, byte[] MODE)
	{
		byte[] build1 = concatenate(header, NAME);
		byte[] build2 = concatenate(build1, zero);
		byte[] build3 = concatenate(build2, MODE);

		byte[] finalBuild = concatenate(build3, zero);

		return finalBuild;
	}

	/**
	 * builds and sends a new Packet
	 * @param msg: the message you want to send
	 * @param len: length of the message
	 * @param desti: destination ip
	 * @param port: destination port
	 * @param s: source socket
	 * @param source: source address
	 */
	public static void sendPacket(byte[]msg, int len, InetAddress desti, int port, DatagramSocket s, String source)
	{
		DatagramPacket packet = buildPacket(msg, len, desti, port);
		System.out.println("The source " + source + " is sending a packet:");

		//prints out information about the packet
		System.out.println("Packet from host: " + packet.getAddress());
		System.out.println("From host port: " + packet.getPort());
		System.out.println("Length: " + packet.getLength());
		System.out.print("Containing: " );
		print(msg, msg.length);

		try
		{
			s.send(packet);
		} catch (IOException ie)
		{
			ie.printStackTrace();
			System.exit(1);
		}
		System.out.println(source + ": packet sent\n");
	}

	/**
	 * wait for a packet from host, when received, prints out its information as well as its content
	 * @param s: DatagramSocket to receive
	 * @param source: source host
	 * @return the packet received
	 */
	public static DatagramPacket waitPacket(DatagramSocket s, String source)
	{
		byte msg[] = new byte[100];
		DatagramPacket receivedPacket = new DatagramPacket(msg, msg.length);
		System.out.println("The source " + source + " is waiting for a packet");

		try
		{
			System.out.println("waiting...");
			s.receive(receivedPacket);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}

		System.out.println("The source " + source + " has received the packet");

		//prints out information about the packet
		System.out.println("Packet from host: " + receivedPacket.getAddress());
		System.out.println("From host port: " + receivedPacket.getPort());
		System.out.println("Length: " + receivedPacket.getLength());
		System.out.print("Containing: " );
		print(msg, msg.length);

		return receivedPacket;
	}

	/**
	 * prints out the contents of a byte array
	 * @param bytes: the byte array
	 * @param len: length of the byte array
	 */
	private static void print(byte[] bytes, int len)
	{
		System.out.print("Data as bytes: ");
		for (int i=0; i<len; i++) {
			System.out.print(Integer.toHexString(bytes[i]));
			System.out.print(' ');
		}
		System.out.print("\n");

		System.out.print("Data as string: ");
		for (int i=0; i<len; i++) {
			if (bytes[i] < 32) {
				System.out.print((char) (bytes[i] + '0'));
			}
			else {
				System.out.print((char) bytes[i]);
			}
			System.out.print(' ');
		}
		System.out.print("\n\n");
	}

	/**
	 * builds a new packet
	 * @param msg: the message you want to convert
	 * @param len: length of the message
	 * @param desti: destination address
	 * @param port: destination port
	 */
	public static DatagramPacket buildPacket(byte[]msg, int len, InetAddress desti, int port)
	{
		DatagramPacket packet = new DatagramPacket(msg, len, desti, port);
		return packet;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		/**
		// Test code for msgBuilder and printing
		byte[] in = msgBuilder(true, "inn!", "netascii");
		byte[] out = msgBuilder(false, "out!", "octet");
		for(int i=0; i< in.length ; i++) {
			System.out.print(in[i] +" ");
		}
		System.out.println("");
		for(int i=0; i< out.length ; i++) {
			System.out.print(out[i] +" ");
		}
		*/
		Client c = new Client();
		boolean r = true;
		for (int i = 0; i<10; i++)
		{
			String s = "testfile.txt";
			byte[] msg = msgBuilder(r, s, "octet");
			c.sendAndReceive(msg, IntHost.port);
			r = !r;
		}
		byte[] err = msgBuilder(false, "invalid request", "netascii");
		c.sendAndReceive(err, IntHost.port);
		c.sendReceiveSocket.close();
	}

}
