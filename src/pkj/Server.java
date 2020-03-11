/**
 * 
 */
package pkj;

import java.io.*;
import java.net.*;

/**
 * @author Jia Chen Huang
 * @verison Feb 3, 2020
 *
 */
public class Server
{

	private static DatagramPacket receivePacket;
	private static DatagramSocket sendSocket, receiveSocket;
	byte[] readRequest = {0, 3, 0, 1};
	byte[] writeRequest = {0, 4, 0, 1};
	//private boolean running;
	//private byte[] buf = new byte[256];
	public static final int port = 69;

	/**
	 * Constructor
	 */
	public Server()
	{
		try
		{
			sendSocket = new DatagramSocket();
			receiveSocket = new DatagramSocket(port);
		}
		catch (SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * echoes the datapack back to client
	 * @throws Exception
	 */
	private void echo()
	{
		receivePacket = Client.waitPacket(receiveSocket, "Server");
		byte[] data = receivePacket.getData();
		if(verify(data))
		{
			byte[] replyData;
			if(data[1] == 1)
			{
				replyData = readRequest;
			}
			else
			{
				replyData = writeRequest;
			}
			System.out.println("Server: Packet sending");
			sendPacket(replyData, replyData.length, receivePacket.getAddress(), IntHost.port, sendSocket, "Server");
			System.out.println("Send Packet: Success");
		}
		return;
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
	 * verify data packet has the right format
	 * @param data: byte array to be verified
	 * @return true for no error, false for error
	 */
	private boolean verify(byte[] data)
	{
		int zerosCount =0;
		boolean one=false,two=false,three=true,four=false;

		if(data[0]==0)
		{
			one=true;
			zerosCount++;
		}
		if(data[1]==1 || data[1]==2)
		{
			two=true;
		}
		for(int i=2; i<data.length; i++)
		{
			if(data[i]<32 && data[i]!=0)
			{
				three=false;
			}
			if(data[i]==0)
			{
				zerosCount++;
			}
		}
		if(data[data.length-1]==0)
		{
			four=true;
		}
		return(one&&two&&three&&four&&zerosCount>=3); 
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server s = new Server();
		while(true)
		{
			s.echo();
		}

	}

}
