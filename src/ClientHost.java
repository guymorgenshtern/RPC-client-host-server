import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/*
* client sends packet with data to host1
* packet is put into dataBuffer
* host1 sends echo to client
* 
* client sends request to host1
* host1 retrieves data from acknowledgementBuffer
* sends it to client
*/
public class ClientHost extends Host{

	public ClientHost(Buffer dataBuffer, Buffer acknowledgementBuffer, int port) {
		super(dataBuffer, acknowledgementBuffer, port);
		
	}

	@Override
	public void run() {
		while (true) {
			receiveAndEcho(this.dataBuffer);
			requestData(this.acknowledgementBuffer);
		}
	}

}
