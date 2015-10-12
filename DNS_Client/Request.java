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

public class Request {

	public static final int TIMEOUT_DEFAULT = 5; 
	public static final int MAX_RETRIES_DEFAULT = 3;
	public static final int PORT_DEFAULT = 53; 
	public static final int TYPE_A_QUERY=0;
	public static final int TYPE_NS_QUERY=1;
	public static final int TYPE_MX_QUERY=2;

	private int timeout,maxRetries,port,type ;


	public Request() {
		timeout = TIMEOUT_DEFAULT; 
		maxRetries = MAX_RETRIES_DEFAULT; 
		port = PORT_DEFAULT; 
		type =TYPE_A_QUERY;
	}

	public void setTimeOut (int time) { this.timeout = time; }
	public int getTimeOut () { return this.timeout; }

	public void setMaxRetries (int retries) { this.maxRetries = retries ;}
	public int getMaxRetries () { return this.maxRetries;  }

	public void setPort (int port) { this.port = port ; }
	public int getPort () { return this.port; }

	public void setType (int port) { this.type = type ; }
	public int getType () { return this.type; }

}