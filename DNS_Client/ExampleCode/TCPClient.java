/**
 * TCPClient
 * 
 * Adapted from the example given in Section 2.7 of Kurose and Ross, Computer
 * Networking: A Top-Down Approach (5th edition)
 * 
 * @author michaelrabbat
 *
 */
import java.io.*;
import java.net.*;

public class TCPClient {
	public static void main(String argv[]) throws Exception
	{
		// Create two string variables to hold the info we'll send and receive from the server
		String sentence;
		String modifiedSentence;
		
		// Open a reader to input from the command line
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		
		// Open a TCP socket to the server, running on port 6789 "localhost" (i.e., this machine)  
		Socket clientSocket = new Socket("localhost", 6789);
		
		// Open readers to send/receive from server
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		// Read input from the user:
		System.out.println("Type a message and hit enter.");
		sentence = inFromUser.readLine();
		
		// Send the message to the server
		outToServer.writeBytes(sentence + '\n');
		
		// Read the response from the server
		modifiedSentence = inFromServer.readLine();
		System.out.println("From Server: " + modifiedSentence);
		
		// Close the socket
		//clientSocket.close();
	}
}
