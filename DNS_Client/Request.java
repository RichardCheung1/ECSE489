/*
ECSE 489 Experiment 2
Networking Programming and DNS

Ryan Martis		260465757
Richard Cheung	260494981
Wed08
DnsRequest.java
*/
package DNS; 

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

public class Request {

	public static final int TIMEOUT_DEFAULT = 5000; 
	public static final int MAX_RETRIES_DEFAULT = 3;
	public static final int PORT_DEFAULT = 53; 
	public static final int TYPE_A_QUERY=0;
	public static final int TYPE_NS_QUERY=1;
	public static final int TYPE_MX_QUERY=2;
	public static final String IPADDRESS_PATTERN = 
		"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
		"([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private int timeout,maxRetries,port,type ;
	private String ipAddress;


	public Request() {
	}

	public Request(String ipAddress) {
		this.timeout = TIMEOUT_DEFAULT; 
		this.maxRetries = MAX_RETRIES_DEFAULT; 
		this.port = PORT_DEFAULT; 
		this.type =TYPE_A_QUERY;
		this.ipAddress = ipAddress;
	}

	public void setTimeOut (int time) { this.timeout = time*1000; }
	public int getTimeOut () { return this.timeout; }

	public void setMaxRetries (int retries) { this.maxRetries = retries ;}
	public int getMaxRetries () { return this.maxRetries;  }

	public void setPort (int port) { this.port = port ; }
	public int getPort () { return this.port; }

	public void setType (int type) { this.type = type ; }
	public int getType () { return this.type; }
	public String getStringType () {
		if (getType() ==TYPE_NS_QUERY ) {
			return "NS";
		}
		if (getType() ==TYPE_MX_QUERY ) {
			return "MX";
		}
		return "A";
	}

	private void setIpAddr (String ipAddr) { this.ipAddress = ipAddr ; }
	public String getIpAddr () { return this.ipAddress; }


	public InetAddress ipData() throws Exception {
		String ipAddr = this.ipAddress;
		int[] ipV4 = new int[4];
		int counter=0;
		for (String s : ipAddr.split("\\.") ) {
			ipV4[counter]= Integer.valueOf(s);
			counter++;
		}
		InetAddress addr1 = InetAddress.getByAddress(new byte [] {(byte)ipV4[0],(byte)ipV4[1],(byte)ipV4[2],(byte)ipV4[3]});
		return addr1;
	}

	public boolean ipAddressValidator () {
		String ipAddr = getIpAddr();
		if (ipAddr.contains("@")){
			setIpAddr(ipAddr.substring(1));
		}
		else{
			System.out.println("ERROR 	IP address not valid: Enter IPv4 in @a.b.c.d format");			
			return false;
		}
		if (!ipPatternValidator(getIpAddr()) || getIpAddr().contains("@")) {
			System.out.println("ERROR 	IP address not valid: Enter IPv4 in @a.b.c.d format");
			return false;
		}
		return true;
	}

	public boolean ipPatternValidator (String ip) {
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN); 
		Matcher matcher = pattern.matcher(ip);
		return matcher.matches(); 
	}

	public boolean isNumeric(String str)  
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


	public void syntaxError() {
		System.out.println("ERROR 	nDnsClient [-t timeout] [-r max-retries] [-p port] [-mx/-ns] @server name");
	}
}