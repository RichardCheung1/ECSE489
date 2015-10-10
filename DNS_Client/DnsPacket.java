/*
ECSE 489 Experiment 2
Networking Programming and DNS

Ryan Martis		260465757
Richard Cheung	260494981
Wed08
DnsPacket.java
*/

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class DnsPacket {

	private ByteBuffer dnsPacketHeader;
	private ByteBuffer dnsPacketQuestion; 
	private ByteBuffer dnsPacketAnswers;

	private ByteBuffer dnsPacket; 

	public ByteBuffer createPacketHeader(boolean qr, boolean aa){
		
	} 

}