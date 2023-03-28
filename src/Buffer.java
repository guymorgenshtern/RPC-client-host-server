import java.net.DatagramPacket;
import java.util.ArrayDeque;


public class Buffer {
	private ArrayDeque<DatagramPacket> buffer;
	private int maxSize;
	
	public Buffer(int size) {
		this.buffer = new ArrayDeque<>();
		this.maxSize = size;
	}
	
	/**
	 * removes and returns top most data from buffer
	 * @return DatagramPacket - data
	 */
	synchronized public DatagramPacket get() {
		while (buffer.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		DatagramPacket packet = buffer.poll();
		notifyAll();
		return packet;
		
	}
	
	/**
	 * put data into buffer
	 * @param packet - DatagramPacket of data
	 */
	synchronized public void put(DatagramPacket packet) {
		while (buffer.size() == this.maxSize) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		buffer.offer(packet);
		notifyAll();
	}
	
	/**
	 * returns top most piece of data without removing it
	 * @return DatagramPacket
	 */
	synchronized public DatagramPacket peek() {
		DatagramPacket packet = buffer.peek();
		notifyAll();
		return packet;
	}
	
	/**
	 * returns if buffer is empty
	 * @return boolean - true if buffer is empty, false otherwise
	 */
	public boolean isEmpty() {
		return buffer.size() == 0;
	}

}
