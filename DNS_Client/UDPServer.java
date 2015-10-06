/**
 * UDPServer
 * 
 * Adapted from the example given in Section 2.8 of Kurose and Ross, Computer
 * Networking: A Top-Down Approach (5th edition)
 * 
 * @author michaelrabbat
 *
 */
import java.net.*;

public class UDPServer {
	public static void main(String args[]) throws Exception
	{
		// Create a UDP socket on port 9876
		DatagramSocket serverSocket = new DatagramSocket(9876);
		
		// Allocate space for the received and response messages 
		byte[] receiveData = new byte[1024];
		byte[] sendData = new byte[1024];
		
		// Enter an infinite loop
		while (true)
		{
			// Allocate space to receive an incoming packet
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			
			// Receive a packet from the client
			// This call blocks until a packet is received
			serverSocket.receive(receivePacket);
			
			// If we get here, then we received a packet
			System.out.println("Packet received...");

			String sentence = new String(receivePacket.getData());
			System.out.println("From client: " + sentence);

			// Grab the sender's IP address and port
			InetAddress ipAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			
			// Convert the sentence to all caps
			String capitalizedSentence = sentence.toUpperCase();
			
			// Convert the String to a byte array
			sendData = capitalizedSentence.getBytes();
			
			// Make the UDP packet with the response message
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, port);
			
			// Send the UDP packet back to the client
			serverSocket.send(sendPacket);
		}
	}
}
