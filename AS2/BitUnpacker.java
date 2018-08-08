// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.util.BitSet;
import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * This class is for unpacking bits for LZW decoder
 */
class BitUnpacker{
	
	/**
     * Main method for Bit unpacking
     *
     * @throws IOException IO exception will be thrown if IO stream meets error 
     */
	public static void main(String[] args){
		//Initial size for dicitonary
		int dictionarySize = 256; 
		//9 to include reset symbol
		int bitsPerChar = 9;
		// value that once dictionarySize goes over the bits must increase by 1
		int bitIncreaseSize = 256; 
		
		// Input stream wrapper on system.in to read bytes
		BufferedInputStream inStream = new BufferedInputStream(System.in);
		//In and out buffers for bytes to read/write
		byte[] inBuff = new byte[1];
		byte[] outBuff;
		//Bitsets to store the last byte read in and a bitset to build the int
		BitSet inBits = new BitSet(32);
		BitSet nextOutBits = new BitSet(32);
		//Pointers for the current bit to get/set from the bitsets
		int currentOutBit = -1; //pointer to the next bit to set
		int currentInBit = -1; //pointer to the next bit to grab
		int outInt;
		
		try{
			int readEof = 0;
			// Loop while not EOF
			while(readEof != -1){
				nextOutBits = new BitSet(32);
				//Set the input buffer to the read byte
				if(currentInBit == -1){
					// Read in next byte to inBuff
					readEof = inStream.read(inBuff);
					// Convert the byte[] to a BitSet
					inBits = inBits.valueOf(inBuff);
					// Set the In pointer back to the front
					currentInBit = 7;
				}
				//Set the pointer for setting to the most significant bit for current size of bitsPerChar
				currentOutBit = bitsPerChar-1;
				//While the out Bitset hasn't been completed
				while(currentOutBit != -1 && readEof != -1){
					//While neither of the bitsets have reached the end
					while(currentInBit != -1 && currentOutBit != -1){
						//Set the outBit at its current marker to the bit at inBits currentmarker
						nextOutBits.set(currentOutBit,inBits.get(currentInBit));  
						//decriment the pointers to the next most significant bit
						currentOutBit--;
						currentInBit--;
					}
					//Check if loop broke due to inBits reaching end before outBits
					if(currentInBit == -1 && currentOutBit != -1){
						// Read in next byte to inBuff
						readEof = inStream.read(inBuff);
						if(readEof != -1){
							// Convert the byte[] to a BitSet
							inBits = inBits.valueOf(inBuff);
							// Set the In pointer back to the front
							currentInBit = 7;
						}
					}
				
				}
				//Converts the bitSet to a byte[]
				outBuff = nextOutBits.toByteArray();
				//Convert the byte[] to a int
				switch(outBuff.length){
					case 1:	outInt = ((0xff << 8) + (int)outBuff[0] & 0xff);
							break;
					case 2: outInt = (((int)outBuff[1] & 0xff)<< 8) + (outBuff[0] & 0xff); 
							break;
					case 3: outInt = (((int)outBuff[2] & 0xff)<< 16) + (((int)outBuff[1] & 0xff)<< 8) + (outBuff[0] & 0xff); 
							break;
					case 4: outInt = (((int)outBuff[3] & 0xff)<< 24) + (((int)outBuff[2] & 0xff)<< 16) + (((int)outBuff[1] & 0xff)<< 8) + (outBuff[0] & 0xff); 
							break;
					default: outInt = 0;
							break;
				}
				//Check if the int is a reset marker (larger than the dictionarySize)
				if(outInt > dictionarySize){
					dictionarySize = 256;
					bitsPerChar = 9;
					bitIncreaseSize =256;
				}
				else{
					dictionarySize++;
					//check if the size has reached the threshold for incrimenting bitsPerChar
					if(dictionarySize > bitIncreaseSize){
						bitsPerChar++;
						bitIncreaseSize = 2*bitIncreaseSize;
					}
				}
				// Print out the int to system.out only if the currentOutBit is valid
				if(currentOutBit == -1)	System.out.println(outInt);
			}
		}
		catch(IOException e){
			System.err.println(e.getMessage());
		}
	}
}