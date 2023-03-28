import java.net.DatagramPacket;

public class Utils {
	private static final byte[] ECHO = new String("-e").getBytes();
	
	public static void printPacketInfo(DatagramPacket packet) {
		System.out.println("Sender: " + packet.getAddress() + " Port: " + packet.getPort());
		System.out.println("Data:" );
		for (int i = 0; i < packet.getLength(); i++) {
			System.out.print(packet.getData()[i]);
		}
		
		String s = new String(packet.getData());
		System.out.println("\n" + s);
		System.out.println("-------------------------------");
	}
	
	 public static byte[] buildEchoMessage(DatagramPacket packet) {
			byte[] echoMessage = new byte[packet.getLength() + ECHO.length];
			
			for (int j = 0; j < packet.getLength(); j++) {
				echoMessage[j]= packet.getData()[j]; 
			}
			for (int i = 0; i < ECHO.length; i++) {
				echoMessage[packet.getLength() + i] = ECHO[i];
			}
			return echoMessage;
	 }
}
