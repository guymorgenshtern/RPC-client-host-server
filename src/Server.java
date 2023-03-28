import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

public class Server {
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendReceieveSocket;
	private final int MAX_ZERO_BYTES = 2;
	private final int ASCII_RANGE_MIN = 32; //ascii range beginning at "space" character
	private final int ASCII_RANGE_MAX = 126; //ends at ~, includes 0-9 a-Z, and a number of special characters
	private DatagramPacket dataReceived;

	/**
	 * constructor for client, creates the send/receive socket
	 */
	public Server() {
		try {
			sendReceieveSocket = new DatagramSocket();
			sendReceieveSocket.setSoTimeout(60000);
		} catch (SocketException se) { 
			se.printStackTrace();
			System.exit(1);
		}

	}
	
	/**
	 * validate received packet
	 * @param packet
	 * @return
	 */
	private boolean validatePacket(DatagramPacket packet) {
		int numZeroBytes = 0;
		int endOfMessage = 0;
		
		// begins with 0
		if (packet.getData()[0] != (byte)(0)) {
			return false;
			
		}
		
		//followed by 1 for read or 2 for write
		if ( !(packet.getData()[1] == (byte)(1) || packet.getData()[1] == (byte)(2) )) {
			return false;
		};
		
		//includes 2 separation 0 bytes and ASCII characters in between
		for (int i = 2; i < packet.getLength(); i++) {
			if (packet.getData()[i] == (byte)(0)) {
				numZeroBytes++;
			} else if (!(packet.getData()[i] >= ASCII_RANGE_MIN && packet.getData()[i] <= ASCII_RANGE_MAX)){
				return false;
			}
			
			if (numZeroBytes > MAX_ZERO_BYTES) {
				return false;
			}

		}
		
		return true;

	}
	

	/**
	 * Decode a packet into a message object
	 * @param data - byte[] to decode
	 * @return Message 
	 * @throws Exception 
	 */
	private Message decode(DatagramPacket packet) throws Exception {
		
		MessageType type = packet.getData()[1] == (byte) 0001 ? MessageType.READ : MessageType.WRITE;
		String fileName = "";
		int endOfFileName = 0;
		int endOfMessage = 0;

		if (!validatePacket(packet)) {
			throw new Exception("Invalid packet format");
		}
		
		int pointer = 2;
		//find first 0 byte used as separator
		while(pointer < packet.getLength() && endOfFileName == 0) {
			if (packet.getData()[pointer] == (byte) 0) {
				byte[] n = Arrays.copyOfRange(packet.getData(), 2, pointer);
				fileName = new String(n);
				endOfFileName = pointer;
			}
			pointer++;
		}

		String mode = new String (Arrays.copyOfRange(packet.getData(), endOfFileName, packet.getLength()));

		return new Message(type, fileName, mode);
	}

	/**
	 * sends an update request to serverHost and waits for data to be sent
	 */
	private void requestUpdate() {
		System.out.println("\nServer requesting data from host");
		byte[] request = new String("server requesting data").getBytes();
		
		DatagramPacket requestPacket = null;
		try {
			requestPacket = new DatagramPacket(request, request.length, InetAddress.getLocalHost(), 69);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			sendReceieveSocket.send(requestPacket);
			Utils.printPacketInfo(requestPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte data[] = new byte[150];
		receivePacket = new DatagramPacket(data, data.length);

		
		try {
			sendReceieveSocket.receive(receivePacket);
			
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		Message received;
		this.dataReceived = receivePacket;
		try {
			System.out.println("\nServer received data from host");
			Utils.printPacketInfo(receivePacket);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	/**
	 * sends acknowledgement of data received back to host and wait's for an echo
	 *
	 */
	private void acknowledge() {
		
		Message msg = null;
		try {
			msg = decode(dataReceived);
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		byte[] returnMessage;
		if (msg.getType() == MessageType.READ) {
			returnMessage = new byte[] {(byte)(0), (byte)(3), (byte)(0), (byte)(1)};
		} else {
			returnMessage = new byte[] {(byte)(0), (byte)(4), (byte)(0), (byte)(0)};
		}

		System.out.println("\nserver sending acknowledgement");
		DatagramPacket returnPacket = null;
		try {
			returnPacket = new DatagramPacket(returnMessage, returnMessage.length, InetAddress.getLocalHost(), 69);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			sendReceieveSocket.send(returnPacket);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		Utils.printPacketInfo(returnPacket);
		
		byte data[] = new byte[150];
		receivePacket = new DatagramPacket(data, data.length);

		
		try {
			sendReceieveSocket.receive(receivePacket);
			System.out.println("\nserver receving echo from host");
			Utils.printPacketInfo(receivePacket);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main (String[] args) {
		Server s = new Server();

		while(true) {
			s.requestUpdate();
			s.acknowledge();
		}
		
		
	}
}
