/*
ECSE 489 Experiment 2
Networking Programming and DNS

Ryan Martis		260465757
Richard Cheung	260494981
Wed08
DnsClient.java
*/
package DNS; 

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.math.BigInteger;



public class Client {
	 
	//Format for ip address 

	private static DNS.Request request; 
	private static DNS.Packet packet;


	public static void main (String[] args) throws Exception{
		int argsArraySize = args.length;		
		//Get name from command line
		String name = args[argsArraySize-1]; 
		//Get IP address from command line
		String ip = args[argsArraySize-2]; 
		request = new Request(ip); 
		//Must have at least two field
		if (argsArraySize < 2) {
			System.out.println("ERROR 	Missing required arguments: [@server] [name] are required"); 
			return;
		}
		//If there's two or more field check for valid IP
		if (argsArraySize >= 2) {
			if(!request.ipAddressValidator()){
				return;
			} 
		}
		boolean error = true; 
		DatagramSocket clientSocket = new DatagramSocket();

		//initiate Request
		for (int i = 0 ; i< argsArraySize; i++ ) {
			//setting Request options
			 switch (args[i]) {
			 	case "-t":
			 		error= request.isNumeric(args[i+1]);
					int tvalue = Integer.parseInt(args[i+1]);
			 		request.setTimeOut(tvalue);
			 		//System.out.println("Set timeout to: "+ request.getTimeOut() );			 		
			 		break;	
			 	case "-r":
			 		error= request.isNumeric(args[i+1]);
					int rValue = Integer.parseInt(args[i+1]);
			 		request.setMaxRetries(rValue); 	
			 		//System.out.println("Set retries to: "+ request.getMaxRetries() );			 				 			 	
			 		break;
			 	case "-p":
			 		error= request.isNumeric(args[i+1]);
					int portValue = Integer.parseInt(args[i+1]);
			 		request.setPort(portValue); 		
			 		//System.out.println("Set port to: "+ request.getPort() );
			 		break;
			 	case "-mx":
			 		request.setType(request.TYPE_MX_QUERY); 
			 		break;
			 	case "-ns":
			 		request.setType(request.TYPE_NS_QUERY);
			 		break; 			
			 }
			 if (!error) {
			 	System.out.println("ERROR 	Incorrect Options Syntax: Options [-t] [-r] [-p] only take numeric values"); 
			 	return; 
			 }
		}
		packet = new Packet(name, request.getType() ); 

		clientSocket.setSoTimeout(request.getTimeOut());	
		byte[] receiveData = new byte[1024];
		byte[] packetData = packet.data();
		int counter = 0;
		boolean passed = false; 
		InetAddress ipAddress = request.ipData();
		DatagramPacket sendPacket = new DatagramPacket(packetData, packetData.length, ipAddress, request.getPort());
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		long start =0;
		long stop =0;
		loop:
		while (true) {
			if (passed) {
				break loop;
			}
			try {
				System.out.println("DnsClient sending request for "+name); 
				System.out.println("Server: "+ request.getIpAddr());
				System.out.println("Request Type: " + request.getStringType());
		        start = System.currentTimeMillis( );
				clientSocket.send(sendPacket);
				clientSocket.receive(receivePacket);				
				passed= true;
			}
			catch ( SocketTimeoutException e ){
				if (counter == request.getMaxRetries()){
					System.out.println("ERROR 	Maximum number of retires "+ request.getMaxRetries() + " exceeded");
					break loop;
				}
				System.out.println("Retrying...");
				counter++;
			}
			if( passed ){
			    stop = System.currentTimeMillis( );
			    System.out.println("Response received after "+(double)(stop-start)/1000+" seconds "+ counter+" retries");				
				packet.decodeAnswer(receivePacket.getData());
				clientSocket.close();
			}
		}
	}
}
