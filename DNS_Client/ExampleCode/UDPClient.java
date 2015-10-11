/**
 * UDPClient
 * 
 * Adapted from the example given in Section 2.8 of Kurose and Ross, Computer
 * Networking: A Top-Down Approach (5th edition)
 * 
 * @author michaelrabbat
 * 
 */
import java.io.*;
import java.net.*;

public class UDPClient {

	public static void main(String args[]) throws Exception
	{
		// Open a reader to input from the command line
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));

		// Create a UDP socket
		// (Note, when no port number is specified, the OS will assign an arbitrary one)
		DatagramSocket clientSocket = new DatagramSocket();
		
		// Resolve a domain name to an IP address object
		// In this case, "localhost" maps to the so-called loop-back address, 127.0.0.1
		InetAddress ipAddress = InetAddress.getByName("localhost");
		
		// Allocate buffers for the data to be sent and received
		byte[] sendData = new byte[1024];		
		byte[] receiveData = new byte[1024];
		
		// Read a sentence from the user
		System.out.println("Type a message and hit enter.");
		String sentence = inFromUser.readLine();

		// Convert the sentence from a String to an array of bytes
		sendData = sentence.getBytes();
		
		// Create a UDP packet to be sent to the server
		// This involves specifying the sender's address and port number
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ipAddress, 9876);
		
		// Send the packet
		clientSocket.send(sendPacket);
		
		// Create a packet structure to store data sent back by the server
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		
		// Receive data from the server
		clientSocket.receive(receivePacket);
		
		// Extract the sentence (as a String object) from the received byte stream
		String modifiedSentence = new String(receivePacket.getData());
		System.out.println("From Server: " + modifiedSentence);
		
		// Close the socket
		clientSocket.close();
	}
}
