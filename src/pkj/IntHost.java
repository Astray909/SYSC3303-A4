/**
 * 
 */
package pkj;

import java.net.*;
import java.lang.management.*;
import java.util.*;

/**
 * @author Jia Chen Huang
 * @version March 11, 2020
 *
 */
public class IntHost
{
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	static final String INTHOST = "Intermediate Server";
	public static final int port = 23;

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
			//new IntHost().measure();
		}
		
	}
	
	private void measure() {
		 
		ThreadInfo[] threadInfos = threadMxBean.dumpAllThreads(false, false);
		for (ThreadInfo info : threadInfos) {
			threadInitialCPU.put(info.getThreadId(),
					threadMxBean.getThreadCpuTime(info.getThreadId()));
		}
 
		try {
			Thread.sleep(sampleTime);
		} catch (InterruptedException e) {
		}
 
		long upTime = runtimeMxBean.getUptime();
 
		Map<Long, Long> threadCurrentCPU = new HashMap<Long, Long>();
		threadInfos = threadMxBean.dumpAllThreads(false, false);
		for (ThreadInfo info : threadInfos) {
			threadCurrentCPU.put(info.getThreadId(),
					threadMxBean.getThreadCpuTime(info.getThreadId()));
		}
 
		// CPU over all processes
		int nrCPUs = osMxBean.getAvailableProcessors();
		// total CPU: CPU % can be more than 100% (devided over multiple cpus)
		//long nrCPUs = 1;
		// elapsedTime is in ms.
		long elapsedTime = (upTime - initialUptime);
		for (ThreadInfo info : threadInfos) {
			// elapsedCpu is in ns
			Long initialCPU = threadInitialCPU.get(info.getThreadId());
			if (initialCPU != null) {
				long elapsedCpu = threadCurrentCPU.get(info.getThreadId())
						- initialCPU;
				float cpuUsage = elapsedCpu * 100/ (elapsedTime * 1000000F * nrCPUs);
				threadCPUUsage.put(info.getThreadId(), cpuUsage);
			}
		}
 
		// threadCPUUsage contains cpu % per thread
		System.out.println(threadCPUUsage);
		// You can use osMxBean.getThreadInfo(theadId) to get information on
		// every thread reported in threadCPUUsage and analyze the most CPU
		// intentive threads
 
	}

}
