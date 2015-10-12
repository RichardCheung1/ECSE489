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
		this.qClass = 0x00f;
	}

	public byte[] data() {
		String name = this.name; 
		int type = this.type; 
		System.out.println(name+type);
		packetHeader();
		packetQuestion(name,type);
		ByteBuffer data =  ByteBuffer.allocate(1500);
		data.putShort(id);
		data.putShort(flags);
		data.putShort(qdCount);
		data.putShort(anCount);
		data.putShort(nsCount);
		data.putShort(arCount);
		data.put(qName);
		data.putShort(qType);
		data.putShort(qClass);
		//for (int i =0; i<3; i++ ) {
			data.putShort(aName);
			data.putShort(aType);
			//data.putShort(aClass);
			//data.putShort(aTtl);
			//data.putShort(aRdLength);
			//data.putShort(aPreference);
			//data.put(aExchange);
		//}
		System.out.println(new String(data.array(),StandardCharsets.US_ASCII ));
		return data.array();

	}

	private void packetAnswer() {
		this.aName = this.aType = this.aClass = this.aTtl = this.aRdLength = this.aPreference = 0x0000;
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
		return data.array() ;
	}

	private short idGenerator() {
		rng = new Random(); 
		short id = (short) rng.nextInt(Short.MAX_VALUE+1);
		return id; 
	}
	
	/*private void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
   }*/
}