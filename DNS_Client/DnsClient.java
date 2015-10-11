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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DnsClient {
	 
	//Format for ip address 
	private static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
	private static DNS.DnsRequest request; 


	public static void main (String[] args) {
		int argsArraySize = args.length;

		String ip = args[argsArraySize-2].substring(1); 

		//Must have at least two field
		if (argsArraySize < 2) {
			syntaxError("Missing required arguments"); 
			return;
		}
		if (argsArraySize == 2) {
			boolean check = false; 
			check = !ipAddressValidator(ip);
			if (check) {
				syntaxError("IP not valid");
				return;
			}
		}
		if ( !ipAddressValidator(ip) || ip.contains("@")){
			System.out.println("Syntax error on IP adress"); 
			return;
		}
		boolean error = true; 
		request = new DnsRequest(); 
		for (int i = 0 ; i< argsArraySize; i++ ) {
			 switch (args[i]) {
			 	//TO DO: timeout,max-retries,port,mx or ns flag, server, name
			 	case "-t":
			 		error= isNumeric(args[i+1]);
					int tvalue = Integer.parseInt(args[i+1]);
			 		request.setTimeOut(tvalue);
			 		System.out.println("Set timeout to: "+ request.getTimeOut() );			 		
			 		break;	
			 	case "-r":
			 		error= isNumeric(args[i+1]);
					int rvalue = Integer.parseInt(args[i+1]);
	
			 		request.setMaxRetries(rvalue); 	
			 		System.out.println("Set retries to: "+ request.getMaxRetries() );			 				 			 	
			 		break;
			 	case "-p":
			 		error= isNumeric(args[i+1]);
					int pvalue = Integer.parseInt(args[i+1]);

			 		request.setPort(pvalue); 		
			 		System.out.println("Set port to: "+ request.getPort() );
			 		break;
			 	case "-mx":
			 		break;
			 	case "-ns":
			 		break; 			
			 }
			 if (!error) {
			 	syntaxError("Options Syntax"); 
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

	private static void syntaxError(String error) {
		System.out.println("ERROR: "+error+"\nDnsClient [-t timeout] [-r max-retries] [-p port] [-mx/-ns] @server name");
	}


}
