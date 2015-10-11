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

public class DnsRequest {

	private int TIMEOUT_DEFAULT = 5; 
	private int MAX_RETRIES_DEFAULT = 3;
	private int PORT_DEFAULT = 53; 
	private int timeout,maxRetries,port ;


	public DnsRequest() {
		timeout = TIMEOUT_DEFAULT; 
		maxRetries = MAX_RETRIES_DEFAULT; 
		port = PORT_DEFAULT; 
	}

	public void setTimeOut (int time) { this.timeout = time; }
	public int getTimeOut () { return this.timeout; }

	public void setMaxRetries (int retries) { this.maxRetries = retries ;}
	public int getMaxRetries () { return this.maxRetries;  }

	public void setPort (int port) { this.port = port ; }
	public int getPort () { return this.port; }


}