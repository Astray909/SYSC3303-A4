/**
 * 
 */
package pkj;

import java.net.*;
import java.lang.management.*;
import java.util.*;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;

/**
 * @author Jia Chen Huang
 * @version March 11, 2020
 *
 */
public class IntHost extends Thread
{
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	static final String INTHOST = "Intermediate Server";
	static int port = 23;

	private int sampleTime = 20000;
	private ThreadMXBean threadMxBean = ManagementFactory.getThreadMXBean();
	private RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
	private OperatingSystemMXBean osMxBean = ManagementFactory
			.getOperatingSystemMXBean();
	private Map<Long, Long> threadInitialCPU = new HashMap<Long, Long>();
	private Map<Long, Float> threadCPUUsage = new HashMap<Long, Float>();
	private long initialUptime = runtimeMxBean.getUptime();

	/**
	 * constructor
	 */
	public IntHost()
	{
		//this.port = port;
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
	public int port()
	{
		return port;
	}
	 */

	/**
	 * Sends reply packet from server back to client
	 */
	private void reply()
	{
		DatagramPacket receivePacket = Client.waitPacket(receiveSocket, INTHOST);
		int clientPort = receivePacket.getPort();
		//sleepThread(1);
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
		Instant start = Instant.now();
		 
	    //Measure execution time for this method
		runReply();
	 
	    Instant finish = Instant.now();
	 
	    long timeElapsed = Duration.between(start, finish).toMillis();  //in millis
	    
	    System.out.println("time in nanoTime: " + timeElapsed);

	}
	
	private static void runReply()
	{
		System.out.println("Intermediate Host is now running.");
		IntHost i = new IntHost();

		for(int loop = 0; loop < 1001; loop++) {
			i.reply();
		}
		i.sendSocket.close();
		i.receiveSocket.close();
		System.out.println("Intermediate Host has done running.");
	}


	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Intermediate Host is now running.");
		for(int loop = 0; loop < 1001; loop++) {
			reply();
		}
		System.out.println("Intermediate Host has done running.");
	}
}
