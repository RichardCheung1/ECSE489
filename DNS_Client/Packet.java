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
import java.nio.ByteBuffer;
import java.util.Random;


public class Packet {

	private byte[] header;
	private Random rng = new Random(); 

	public Packet {

	}

	private void header() {
		
	}

	private short idGenerator() {
		short id = (short) Random.nextInt(Short.MAX_VALUE + 1);
		return id; 
	}

	private void putShort(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val >>> 0);
        b[off + 0] = (byte) (val >>> 8);
   }
}