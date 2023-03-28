import java.io.IOException;
import java.net.DatagramPacket;

/*
 * server sends request to host2
 * host2 retrieves data from dataBuffer
 * sends it to server
 * 
 * server sends acknowledgment message to host2
 * acknowledgment is placed in acknowledgementBuffer
 */
public class ServerHost extends Host{

	public ServerHost(Buffer dataBuffer, Buffer acknowledgementBuffer, int port) {
		super(dataBuffer, acknowledgementBuffer, port);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		while (true) {
			requestData(this.dataBuffer);
			receiveAndEcho(this.acknowledgementBuffer);
		}
		
	}
}
