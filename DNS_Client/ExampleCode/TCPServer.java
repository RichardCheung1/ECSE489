/**
 * TCPServer
 * 
 * Adapted from the example given in Section 2.7 of Kurose and Ross, Computer
 * Networking: A Top-Down Approach (5th edition)
 * 
 * @author michaelrabbat
 *
 */
import java.io.*;
import java.net.*;

public class TCPServer {
	public static void main(String argv[]) throws Exception
	{
		// Create two string variables, one for 
		String clientSentence;
		String capitalizedSentence;
		
		// Open a ServerSocket on the specified port
		ServerSocket welcomeSocket = new ServerSocket(6789);
		
		while (true)
		{
			// Listen for incoming connections
			// This is a blocking call which only returns when the client has initiated a connection
			Socket connectionSocket = welcomeSocket.accept();

			// If we get here, then we've received a connection
			System.out.println("Connection received...");
			
			// Reader to process incoming data stream from client
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			
			// Writer to send data back to client
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			// Read what the client sent
			clientSentence = inFromClient.readLine();
			System.out.println("From client: " + clientSentence);
			
			Thread.sleep(500);
			
			// Convert to all caps and send back to the client
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
			
			// Do nothing else here... leave it to the client to close the socket.
			// Could also close it here.
		}
	}
}
