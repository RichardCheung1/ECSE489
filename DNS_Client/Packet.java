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
import java.nio.charset.StandardCharsets;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.List;

public class Packet {

	private static final int TYPE_A_QUERY=0;
	private static final int TYPE_NS_QUERY=1;
	private static final int TYPE_MX_QUERY=2;

	private Random rng; 
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
	private short aName;
	private short aType;
	private short aClass;
	private short aTtl;
	private short aRdLength;
	private short aPreference;
	private byte[] aExchange;

//empty constructor
	public Packet() {

	}
	public Packet(String name, int type) {
		packetHeader(); 
		packetQuestion(name,type);
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
		this.qClass = 0x00f;
	}

	/*private void packetAnswer() {
		this.name = this.type = this.class = this.ttl = this.rdLength = this.preference = 0x0000;

	}*/

	private short writeType(int type) {
		short value = 0; 
		switch (type) {
			case TYPE_A_QUERY:
				value = 0x0001;
				break;
			case TYPE_NS_QUERY:
				value = 0x0002;
				break;
			case TYPE_MX_QUERY:
				value = 0x000f;
				break;
		}
		System.out.println("Qtype:"+ value); 
		return value;
	}

	private byte[] writeName (String name) {
		ByteBuffer data =  ByteBuffer.allocate(32);
		char[] buffer= new char[63];
		int dataSize = name.toCharArray().length;
		int counter = 0 ;
		int charCounter = 0;
		for (char charData : name.toCharArray() ) {
			charCounter++;
			if ( charData == '.'){
				data.put(String.valueOf(counter).getBytes(StandardCharsets.US_ASCII));
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
				data.put(String.valueOf(counter).getBytes(StandardCharsets.US_ASCII));
				for (int j = 0; j<counter; j++ ) {
				 	data.put(String.valueOf(buffer[j]).getBytes(StandardCharsets.US_ASCII));
				 } 
				buffer= new char[63];
				counter =0;
			}
		}
		data.put(String.valueOf(0).getBytes(StandardCharsets.US_ASCII));

		System.out.println("Qname: "+new String(data.array(), StandardCharsets.US_ASCII));
		return data.array() ;
	}

	private short idGenerator() {
		rng = new Random(); 
		short id = (short) rng.nextInt(Short.MAX_VALUE+1);
		System.out.println("Packet ID : " +id);
		return id; 
	}

	/*private void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
   }*/
}