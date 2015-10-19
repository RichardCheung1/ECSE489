/*
ECSE 489 Experiment 2
Networking Programming and DNS

Ryan Martis		260465757
Richard Cheung	260494981
Wed08
Packet.java
*/

package DNS; 

import java.io.*;

import java.nio.charset.*;
import java.nio.*;
import java.util.*;
import java.lang.*;

public class Packet {
	//Constants 
	public final int TYPE_A_RR = 0X0001;
	public final int TYPE_NS_RR = 0x0002;
	public final int TYPE_CNAME_RR =0x005;
	public final int TYPE_MX_RR = 0x000f;
	private Random rng;
	private String name;
	private int type; 
	//header fields
	private short id;
	private short flags; 
	private short qdCount; 
	private short anCount; 
	private short nsCount; 
	private short arCount; 

	//question fields
	private byte[] qName; 
	private short qType;
	private short qClass; 
	//RR fields
	private int answerAA;
	private int answerType;
	private int answerClass;
	private int answerTtl;
	private int answerRdLength;
	private String answerRdata;
	private String answerCname;
	private int answerPreference;
	private String answerExchange;
	//cached size
	private int qNameSize;
	private int cNameSize;
	private final int HEADER_SIZE = 12;

//Initiate packet with default values
	public Packet() {

	}
	public Packet(String name, int type) {
		this.name = name;
		this.type = type;
	}

	private void packetHeader() {
		this.id = idGenerator(); 
		this.flags = 0x0100; //256
		this.qdCount = 0x0001;
		this.anCount=this.nsCount=this.arCount = 0x0000; 
	}

	private void packetQuestion(String name, int type ) {
		this.qName = writeName(name);
		this.qType = writeType(type);
		this.qClass = 0x0001;
	}
// Create byte[] packet to DatagramPacket
	public byte[] data() {
		String name = this.name; 
		int type = this.type; 
		packetHeader();
		packetQuestion(name,type);
		ByteBuffer data =  ByteBuffer.allocate(520);
		data.putShort(id);
		data.putShort(flags);
		data.putShort(qdCount);
		data.putShort(anCount);
		data.putShort(nsCount);
		data.putShort(arCount);
		data.put(qName);
		data.putShort(qType);
		data.putShort(qClass);
		return truncateBytebuffer(data).array();

	}

	public void decodeAnswer (byte[] receivedPacket) {
		int qNameSize = this.qNameSize;
		int byteCounter = 0;
		String[] binaryPacket = new String[1500];
		byte byte1;
		ByteBuffer dup = ByteBuffer.wrap(receivedPacket);
		//transforms receivedPackets into unsigned Int 
		while(dup.hasRemaining()) {
			byte1 = dup.get();
			binaryPacket[byteCounter] = String.format("%8s",Integer.toBinaryString(Byte.toUnsignedInt(byte1))).replace(' ', '0');
			byteCounter++;
		}
		int answerCount = Integer.valueOf(binaryPacket[6].concat(binaryPacket[7]),2);
		int authorityCount = Integer.valueOf(binaryPacket[8].concat(binaryPacket[9]),2);
		int addCount = Integer.valueOf(binaryPacket[10].concat(binaryPacket[11]),2);		
		byteCounter = HEADER_SIZE+qNameSize+4;
		StringBuilder sb = new StringBuilder();
		int rcodeCounter = 0;
		for (char c : binaryPacket[3].toCharArray() ) {
			rcodeCounter ++;
			if (rcodeCounter > 4) {
				sb.append(c);
			}
		}
		if (Integer.valueOf(sb.toString(),2) != 0) {
			System.out.println("ERROR		Unexpected Response: 	Incorrect name server");
			return;
		}
		else if (answerCount+authorityCount+addCount > 0){
			if (answerCount >= 0) {
				System.out.println("***Answer Section ("+ answerCount +" records)***");	
			}
			if (authorityCount > 0) {
				System.out.println("***Contains "+ authorityCount+ " Authoritative Resource Records (NO DISPLAY)***");
			}
			for (int pi = 0; pi < answerCount+authorityCount+addCount ; pi++) {
				if( pi == (answerCount+authorityCount)) {
					System.out.println("***Addtional Section ("+ answerCount +" records)***");			
				}
				if( Integer.valueOf(binaryPacket[byteCounter]) == 11000000  ) {
					//gets Flags bytes
					int flagCounter=0; 
					for (char c : binaryPacket[2].toCharArray() ) {
						flagCounter++;
						if (flagCounter == 6){
						//gets the AA flag
							this.answerAA= Integer.valueOf(new String(new char[] {c}));
						}
					}
					//next byte is the pointer HEADER_SIZE+qNameSize+4+1
					byteCounter  = byteCounter+2;
					//gets Type bytes
					this.answerType = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
					byteCounter = byteCounter+2;
					//gets Class bytes
					this.answerClass = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
					//gets TTL bytes
					byteCounter = byteCounter+2;
					sb = new StringBuilder ();
					for ( int i= 0; i <4 ; i++ ) {
						sb.append(binaryPacket[byteCounter+i]);
					}
					byteCounter= byteCounter+4;
					this.answerTtl = Integer.valueOf(sb.toString(),2);
					//gets RDlength bytes
					this.answerRdLength = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
					byteCounter= byteCounter+2;
					//Rdata cases
					if (this.answerType == TYPE_A_RR) {
						//4 octects
						sb = new StringBuilder();
						for (int i = 0; i<4 ; i++ ) {
							sb.append(Integer.valueOf(binaryPacket[byteCounter+i],2).toString());
							if( i<3) {
								sb.append(".");
							}
						}
						this.answerRdata = sb.toString();
						byteCounter = byteCounter+4;
					}
					if (this.answerType == TYPE_NS_RR) {
						int pointer =0; 
						boolean pointerFlag= false;
						loop:
						for(int i =0 ; i<63; i++){
							// if  pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 11000000){
								byteCounter = byteCounter+i;
								pointer = Integer.valueOf(binaryPacket[byteCounter+1],2);
								byteCounter = byteCounter+1;
								this.cNameSize = i;
								pointerFlag = true;
								break loop;
							}
							// if no pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 00000000) {
								byteCounter = byteCounter+i;
								this.cNameSize = i;
								break loop;								
							}
						}
						int cSize = pointerFlag ? (this.cNameSize+1): this.cNameSize;	
						String cname = new String(Arrays.copyOfRange(receivedPacket,byteCounter-cSize,byteCounter),StandardCharsets.US_ASCII);
						int namesize = 0;
						loop:
						for (int w = 0; w < 63 ; w++ ) {
							namesize++;
							if (Integer.valueOf(binaryPacket[pointer+w]) == 00000000) {
								break loop;
							}
						}
						if (pointerFlag == true) {
							String pointerString = new String(Arrays.copyOfRange(receivedPacket,pointer+1,pointer+namesize),StandardCharsets.US_ASCII);
							cname = cname.concat(pointerString);
						}
						int characterCounter = 0;
						sb = new StringBuilder();
						this.answerCname = formatAcsiiString(cname);
						byteCounter = byteCounter+1;					
					}
					if (this.answerType == TYPE_CNAME_RR) {
						int pointer =0; 
						boolean pointerFlag= false;
						loop:
						for(int i =0 ; i<63; i++){
							// if  pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 11000000){
								byteCounter = byteCounter+i;
								pointer = Integer.valueOf(binaryPacket[byteCounter+1],2);
								byteCounter = byteCounter+1;
								this.cNameSize = i;
								pointerFlag = true;
								break loop;
							}
							// if no pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 00000000) {
								byteCounter = byteCounter+i;
								this.cNameSize = i;
								break loop;								
							}
						}
						int cSize = pointerFlag ? (this.cNameSize+1): this.cNameSize;	
						String cname = new String(Arrays.copyOfRange(receivedPacket,byteCounter-cSize,byteCounter),StandardCharsets.US_ASCII);
						int namesize = 0;
						loop:
						for (int w = 0; w < 63 ; w++ ) {
							namesize++;
							if (Integer.valueOf(binaryPacket[pointer+w]) == 00000000) {
								break loop;
							}
						}
						String pointerString = new String(Arrays.copyOfRange(receivedPacket,pointer+1,pointer+namesize),StandardCharsets.US_ASCII);
						cname = cname.concat(pointerString);
						int characterCounter = 0;
						sb = new StringBuilder(); 
						this.answerCname = formatAcsiiString(cname);
						byteCounter = byteCounter+1;
					}
					if (this.answerType == TYPE_MX_RR) {
						int pointer =0; 
						boolean pointerFlag= false;
						this.answerPreference = Integer.valueOf(binaryPacket[byteCounter].concat(binaryPacket[byteCounter+1]),2);
						byteCounter = byteCounter+2;
						loop:
						for(int i =0 ; i<63; i++){
							// if  pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 11000000){
								byteCounter = byteCounter+i;
								pointer = Integer.valueOf(binaryPacket[byteCounter+1],2);
								byteCounter = byteCounter+1;
								this.cNameSize = i;
								pointerFlag = true;
								break loop;
							}
							// if no pointer
							if (Integer.valueOf(binaryPacket[byteCounter+i]) == 00000000) {
								byteCounter = byteCounter+i;
								this.cNameSize = i;
								break loop;								
							}
						}
						int cSize = pointerFlag ? (this.cNameSize+1): this.cNameSize;	
						String cname = new String(Arrays.copyOfRange(receivedPacket,byteCounter-cSize,byteCounter),StandardCharsets.US_ASCII);
						int namesize = 0;
						loop:
						for (int w = 0; w < 63 ; w++ ) {
							namesize++;
							if (Integer.valueOf(binaryPacket[pointer+w]) == 00000000) {
								break loop;
							}
						}
						String pointerString = new String(Arrays.copyOfRange(receivedPacket,pointer+1,pointer+namesize),StandardCharsets.US_ASCII);
						cname = cname.concat(pointerString);
						int characterCounter = 0;
						sb = new StringBuilder(); 
						this.answerCname = formatAcsiiString(cname);
						byteCounter = byteCounter+1;
					}
					printResults(this.answerType);
				}
				else {
					System.out.println("NOTFOUND");
				}
			}
		}
		else {
			System.out.println("NOTFOUND");
		}		
	}

	public String formatAcsiiString (String s ) {
		StringBuilder sb = new StringBuilder(); 
		int length = s.length()-1; 
		int counter = 0;
		//System.out.println(s);
		for (char c : s.toCharArray() ) {
			if (Character.isLetterOrDigit(c)) {
				sb.append(new String(new char [] {c} ));
			}
			else{
				if(counter != 0 && counter != length ) {
					sb.append(".");
				}
			}
			counter++;
		}	
		return sb.toString();
	}

	public void printResults(int type) {
		if (type == TYPE_A_RR){
			//System.out.println("Response Type 	Type A RR"+"	TTL 	"+this.answerTtl);		
			System.out.println("IP 		" + this.answerRdata + " 		"+this.answerTtl+"		"+((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
			//System.out.println("");
		}
		if (type == TYPE_CNAME_RR) {
			System.out.println("CNAME 		" + this.answerCname +" 		"+this.answerTtl+"		"+((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
		if (type == TYPE_NS_RR) {
			System.out.println("NS 		" + this.answerCname + " 		"+this.answerTtl+"		"+((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
		if (type == TYPE_MX_RR) {
			System.out.println("MX 		" + this.answerCname + "		" + this.answerPreference + " 		"+this.answerTtl+"		"+ ((this.answerAA == 0)? "Non-Authoritative":"Authoritative"));
		}
	}

	private short writeType(int type) {
		short value = 0; 
		switch (type) {
			case DNS.Request.TYPE_A_QUERY:
				value = 0x0001;
				break;
			case DNS.Request.TYPE_NS_QUERY:
				value = 0x0002;
				break;
			case DNS.Request.TYPE_MX_QUERY:
				value = 0x000f;
				break;
		}
		return value;
	}

	private byte[] writeName (String name) {
		ByteBuffer data =  ByteBuffer.allocate(32);
		char[] buffer= new char[63];
		int dataSize = name.toCharArray().length;
		byte counter = 0 ;
		int charCounter = 0;
		for (char charData : name.toCharArray() ) {
			charCounter++;
			if ( charData == '.'){
				data.put(counter);
				for (int j = 0; j<counter; j++ ) {
				 	data.put(String.valueOf(buffer[j]).getBytes(StandardCharsets.US_ASCII));
				 } 
				buffer= new char[63];
				counter =0;
			}
			else{
				buffer[counter] = charData;
				counter ++;
			}
			if ( charCounter == dataSize) {
				data.put(counter);
				for (int j = 0; j<counter; j++ ) {
				 	data.put(String.valueOf(buffer[j]).getBytes(StandardCharsets.US_ASCII));
				 } 
				buffer= new char[63];
				counter =0;
			}
		}
		data.put(counter);
		this.qNameSize = truncateBytebuffer(data).capacity(); 
		return truncateBytebuffer(data).array() ;
	}

	private short idGenerator() {
		rng = new Random(); 
		short id = (short) rng.nextInt(Short.MAX_VALUE+1);
		return id; 
	}
	
	private ByteBuffer truncateBytebuffer (ByteBuffer b){
		ByteBuffer copy = b.duplicate();
		b.flip(); 
		int size = 0;
		while (b.hasRemaining()) {
			b.get();
			size++;
		}
		ByteBuffer truncated = ByteBuffer.allocate(size);
		copy.flip();
		while (copy.hasRemaining()) {
			truncated.put(copy.get());
		}
		return truncated;
	}
}