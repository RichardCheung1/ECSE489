/**
 * MultiThreadedTCPServer
 * 
 * Processes each incoming connection in a parallel thread using Java's Runnable class.
 * 
 * @author michaelrabbat, adapted from code provided by Jun Ye Yu
 */

import java.io.*;
import java.net.*;

public class MultiThreadedTCPServer {
	public static void main(String[] args) {
		try {
			ServerSocket welcomeSocket = new ServerSocket(6789);
	
			// Continuous loop
			while (true)
			{
				System.out.println("waiting for client");
				
				// Listen to client request
				Socket connectionSocket = welcomeSocket.accept();
				
				System.out.println("connection established, creating new thread");
				
				// Create a thread to handle the request
				ServerThread handler = new ServerThread(connectionSocket);
				new Thread(handler).start();
			}
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
}


/**
 * This class implements Java's Runnable interface. It is a subclass which is
 * called to handle each incoming connection.
 * 
 * @author michaelrabbat
 * 
 */
class ServerThread implements Runnable
{
	// Socket associated with this thread
	private Socket connectionSocket;
	
	// Constructor, assign a socket to this thread
	public ServerThread(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
	}
	
	// The thread will execute this method then terminate
	@Override
	public void run() {
		String question; // Received from client
		String reply; // Reply to client
		
		try {
			// Establish connection with client
			// Open a stream to read from client
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			// Open a stream to write to client
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	
			// Process client request 
			// Read client's question
			question = inFromClient.readLine();
			
			System.out.println("from client: " + question);
			
			// Convert the sentence to upper case
			reply = question.toUpperCase();
				
			// Send the sentence back to client
			outToClient.writeBytes(reply + '\n'); 
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}