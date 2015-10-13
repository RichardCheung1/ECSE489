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
	private ByteBuffer answerName;
	private ByteBuffer answerType;
	private ByteBuffer answerClass;
	private ByteBuffer answerTtl;
	private ByteBuffer answerRdLength;
	private ByteBuffer answerPreference;
	private ByteBuffer answerExchange;
	//cached size
	private int qNameSize;
	private final int HEADER_SIZE = 12;

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
		this.qClass = 0x0001;
	}


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

	public void packetAnswer(byte[] receivedPacket) {
		int qNameSize = this.qNameSize;
		int byteCounter = 0;
		String[] answerStringByte = new String[1500];
		byte byte1;
		ByteBuffer dup = ByteBuffer.wrap(receivedPacket);
		while(dup.hasRemaining()) {
			byte1 = dup.get();
			//System.out.println(Byte.toUnsignedInt(byte1));
			answerStringByte[byteCounter] = Integer.toBinaryString(Byte.toUnsignedInt(byte1));
			byteCounter++;
		}
		int answerCount = Integer.valueOf(answerStringByte[6].concat(answerStringByte[7]));
		System.out.println("***Answer Section ("+ answerCount+" records)***");

		if (answerCount > 0){

		}
		else {
			System.out.println("NOTFOUND");
		}

		return;
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