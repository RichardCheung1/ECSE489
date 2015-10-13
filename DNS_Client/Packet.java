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
import java.util.Random;
import java.util.List;

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
//	private ByteBuffer answerName;
	private int answerType;
	private int answerClass;
	private int answerTtl;
	private int answerRdLength;
	private String answerRdata;
//	private ByteBuffer answerPreference;
//	private ByteBuffer answerExchange;
	//cached size
	private int qNameSize;
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
		String[] answerStringByte = new String[1500];
		byte byte1;
		ByteBuffer dup = ByteBuffer.wrap(receivedPacket);
		//transforms receivedPackets into unsigned Int 
		while(dup.hasRemaining()) {
			byte1 = dup.get();
			answerStringByte[byteCounter] = String.format("%8s",Integer.toBinaryString(Byte.toUnsignedInt(byte1))).replace(' ', '0');
			byteCounter++;
		}
		int answerCount = Integer.valueOf(answerStringByte[6].concat(answerStringByte[7]));
		System.out.println("***Answer Section ("+ answerCount +" records)***");
		byteCounter = HEADER_SIZE+qNameSize+4;
		if (answerCount > 0){
			if( Integer.valueOf(answerStringByte[byteCounter]) == 11000000  ) {
				//TO DO: Auth bit

				//next byte is the pointer HEADER_SIZE+qNameSize+4+1
				byteCounter  = byteCounter+2;
				//gets Type bytes
				this.answerType = Integer.valueOf(answerStringByte[byteCounter].concat(answerStringByte[byteCounter+1]),2);
				byteCounter = byteCounter+2;
				//gets Class bytes
				this.answerClass = Integer.valueOf(answerStringByte[byteCounter].concat(answerStringByte[byteCounter+1]),2);
				//gets TTL bytes
				byteCounter = byteCounter+2;
				StringBuilder sb = new StringBuilder ();
				for ( int i= 0; i <4 ; i++ ) {
					sb.append(answerStringByte[byteCounter+i]);
				}
				byteCounter= byteCounter+4;
				this.answerTtl = Integer.valueOf(sb.toString(),2);
				//gets RDlength bytes
				this.answerRdLength = Integer.valueOf(answerStringByte[byteCounter].concat(answerStringByte[byteCounter+1]),2);
				byteCounter= byteCounter+2;

				//Rdata cases
				if (this.answerType == TYPE_A_RR) {
					//4 octects
					sb = new StringBuilder();
					for (int i = 0; i<4 ; i++ ) {
						sb.append(Integer.valueOf(answerStringByte[byteCounter+i],2).toString());
						if( i<3) {
							sb.append(".");
						}
					}
					this.answerRdata = sb.toString();
				}
				if (this.answerType == TYPE_NS_RR) {
					//to do
				}
				if (this.answerType == TYPE_CNAME_RR) {
					//to do
				}
				if (this.answerType == TYPE_MX_RR) {
					//to do
				}
				printResults(this.answerType);
			}
		}
		else {
			System.out.println("NOTFOUND");
		}
	}

	public void printResults(int type) {
		if (type == TYPE_A_RR){
			System.out.println("Response Type 	Type A RR"+"	TTL 	"+this.answerTtl);		
			System.out.println("IP 	"+this.answerRdata+" 	[seconds can cache]		[auth/nonauth]");
			System.out.println("");
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

	/*private void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
   }*/
}