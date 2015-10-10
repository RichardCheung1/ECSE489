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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DnsClient {
	//
	//
	private int TIMEOUT_DEFAULT = 5; 
	private int MAX_RETRIES_DEFAULT = 3;
	private int PORT_DEFAULT = 53; 
	private String server, name; 

	private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public static void main (String[] args) {
		int argsArraySize = args.length;
		if (argsArraySize < 2) {
			syntaxError(); 
			return;
		}
		if (!args[argsArraySize-1].contains("@")) {
			syntaxError(); 
			return;
		}
		else {
			String substring = args[argsArraySize-1].substring(1); 
			if (!ipAddressValidator(substring)){
				System.out.println("Syntax error on IP adress"); 
				return;
			}
		}
		boolean flag = false; 
		for (int i = 0 ; i< argsArraySize; i++ ) {
			 switch (args[i]) {
			 	//TO DO: timeout,max-retries,port,mx or ns flag, server, name

			 	case "-t":
			 		flag= isNumeric(args[i+1]);
			 		break;	
			 	case "-r":
			 		flag= isNumeric(args[i+1]);		 	
			 		break;
			 	case "-p":
			 		flag= isNumeric(args[i+1]);			 	
			 		break;
			 	case "-mx":
			 		break;
			 	case "-ns":
			 		break; 			
			 }
			 if (!flag) {
			 	syntaxError(); 
			 	return; 
			 }
		}

	}

	private static boolean ipAddressValidator (String ip) {
		
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN); 
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches(); 
	}

	private static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}

	private static void syntaxError() {
		System.out.println("ERROR: Syntax should be:");
		System.out.println("DnsClient [-t timeout] [-r max-retries] [-p port] [-mx/-ns] @server name");
	}


}
