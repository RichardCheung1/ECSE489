/*
ECSE 489 Experiment 2
Networking Programming and DNS

Ryan Martis		260465757
Richard Cheung	260494981
Wed08
DnsClient.java
*/

import java.io.*;
import java.net.*;

public class DnsClient {
	//
	//
	private int TIMEOUT_DEFAULT = 5; 
	private int MAX_RETRIES_DEFAULT = 3;
	private int PORT_DEFAULT = 53; 
	private String server, name; 

	public static void main (String[] args) {
		int argsArraySize = args.length; 
		for (int i = 0 ; i< argsArraySize; i++ ) {
			 switch (args[i]) {
			 	case "-t":
			 		System.out.println("Timeout set to"+args[i++]); 
			 		break;	
			 	case "-r":
			 		System.out.println("Max retries set to"+args[i++]);
			 		break;
			 	case "-p":
			 		System.out.println("Port set to"+args[i++]);
			 		break;
			 	case "-mx":
			 		System.out.println("Mail server");
			 		break;
			 	case "-ns":
			 		System.out.println("Name Server");
			 		break; 			
			 }
		}

	}

	private static void checkSyntax ( String[] args) {
		int argsArraySize = args.length; 
		for (int i = 0 ; i< argsArraySize; i++ ) {
			if

		}

	}

	private static void syntaxError() {
		System.out.println ("ERROR: Syntax should be DnsClient [-t timeout] [-r max-retries] [-p port] [-mx/-ns] @server name");
	}
	//TO DO: timeout,max-retries,port,mx or ns flag, server, name


}
