import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

/*
 * each host represents 1 half of communication in the system
 * client<->host or server<->host
 * 
 * client sends packet with data to host1
 * packet is put into buffer1
 * host1 sends echo to client
 * 
 * server sends request to host2
 * host2 retrieves data from buffer1
 * sends it to server
 * 
 * server sends acknowledgment message to host2
 * acknowledgment is placed in buffer2
 * 
 * client sends request to host1
 * host1 retrieves data from buffer2
 * sends it to client
 */

public abstract class Host implements Runnable{
	protected DatagramSocket socket; 
	protected Buffer dataBuffer;
	protected Buffer acknowledgementBuffer;
	
	protected InetAddress senderAddress;
	protected int senderPort;
	
	/**
	 * constructor for Host
	 * @param dataBuffer
	 * @param acknowledgementBuffer
	 * @param port
	 */
	 public Host(Buffer dataBuffer, Buffer acknowledgementBuffer, int port) {
		 this.dataBuffer = dataBuffer;
		 this.acknowledgementBuffer = acknowledgementBuffer;
		
		 try {
		        this.socket = new DatagramSocket(port);
		        this.socket.setSoTimeout(60000);
			} catch (SocketException se) { 
				se.printStackTrace();
				System.exit(1);
			}
	}
	
	 /**
	  * concrete implementation for receive and echo, gets packet, adds it to buffer, appends echo suffix to data and sends back
	  * @param buffer - buffer to put data into
	  */
	protected void receiveAndEcho(Buffer buffer) {
		byte data[] = new byte[150];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
 
		try {
			 this.socket.receive(receivePacket);

		} catch(IOException e) {
		     e.printStackTrace();
		     System.exit(1);
		}
		
		this.senderPort = receivePacket.getPort();
		this.senderAddress = receivePacket.getAddress();
		
		Utils.printPacketInfo(receivePacket);
		buffer.put(receivePacket);
		
		byte[] echoMessage = Utils.buildEchoMessage(receivePacket);
		
		DatagramPacket echo = new DatagramPacket(echoMessage, echoMessage.length, this.senderAddress, this.senderPort);
		try {
			System.out.println(socket);
			this.socket.send(echo);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * concrete implementation for requestingData - receives request and retrieves data from correct buffer
	 * @param buffer - Buffer to retrieve from
	 */
	protected void requestData(Buffer buffer) {
		byte byteArr[] = new byte[150];
		DatagramPacket requestPacket = new DatagramPacket(byteArr, byteArr.length);
		
		try {
			 this.socket.receive(requestPacket);

		} catch(IOException e) {
		     e.printStackTrace();
		     System.exit(1);
		}
		
		this.senderPort = requestPacket.getPort();
		this.senderAddress = requestPacket.getAddress();
	
		
		DatagramPacket data = buffer.get();
		DatagramPacket newData = new DatagramPacket(data.getData(), data.getLength(), this.senderAddress, this.senderPort);
		try {
			this.socket.send(newData);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Buffer dataBuffer = new Buffer(1);
		Buffer acknowledgementBuffer = new Buffer(1);
		Thread clientHost = new Thread(new ClientHost(dataBuffer, acknowledgementBuffer, 23));
		Thread serverHost = new Thread(new ServerHost(dataBuffer, acknowledgementBuffer, 69));
		
		clientHost.start();
		serverHost.start();
		
	}
}
