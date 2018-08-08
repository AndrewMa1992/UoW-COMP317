// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * This class is for bit packing
 * The author of this class, Yunhao Fu, hereby, sincerely thank to our great tutor Bryce Nemhauser who patiently answered my questions
 * my nice and patient teammate Jason Hayman who guided me step by step though out this assignment
 * and previous student Dan Collins who put their open source code on github for reference. (https://github.com/dancollins/comp317_lzw/blob/master/Packer.java) *
 * <p>
 * Without their help, I may not be able to fully understand what the magic happened behind bit packing.
 * Thanks again.
 */
public class BitPacker {

    /**
     * Calculate the log value for packing amount of bits
     * This method refers to https://stackoverflow.com/a/680040
     *
     * @param value
     * @return The result of log with base 2 of that value
     */
    public static int log2(int value) {
        return Integer.SIZE - Integer.numberOfLeadingZeros(value);
    }

    /**
     * Main method of big packing
     *
     * @param args
     */
    public static void main(String[] args) {
        //Initial the maximum entries in dictionary
        int maxEntries = 255;
        //Get the current phrase from standard system in
        int currentPhrase = -1;
        //The temp phrase while processing
        int processingPhrase = -1;
        //Flag of how many bits in current processing phrase
        int bitsInPhrase = 0;
        //How many bits in out put each time
        int bitsInOutput = 8;
        //The byte which need to be outputted
        byte outputByte = 0;
        //End of whole bit packing
        byte endOfOutput = 0;
        //Use buffered reader to read from terminal using input stream reader of system.in
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        //Get the input as a string first
        String inputString = "";

        try {
            //While the reader read something from terminal
            while ((inputString = reader.readLine()) != null) {

                //Firstly try to parse the string to integer
                try {
                    currentPhrase = Integer.parseInt(inputString);
                } catch (Exception e) {
                    //If there is nothing parsed in or some other things, just break while loop to output the end of bit packing
                    break;
                }

                //Calculate how many bits we need for this phrase
                //The formula firstly mentioned by our tutor Bryce
                bitsInPhrase = (int) Math.floor(log2(maxEntries)) + 1;
                //Check if it is over the maximum bits

                //While the bits in phrase larger than our bits in output
                //Which need to do processing on packing
                while (bitsInPhrase > bitsInOutput) {
                    //Shift our output byte to left to give enough space for output
                    outputByte <<= bitsInOutput;
                    //Right shift current phrase number with the bits we need to pack
                    processingPhrase = currentPhrase >> (bitsInPhrase - bitsInOutput);
                    //Do a masking job
                    processingPhrase &= (int) (Math.pow(2, bitsInOutput) - 1);
                    //Reduce the bits size in current phrase for remaining bits which not processed here
                    bitsInPhrase -= bitsInOutput;
                    //Do a or calculation to the final output byte
                    outputByte |= processingPhrase;
                    //Write the output to system out
                    System.out.write(outputByte);
					System.out.flush();
                    //Reset the output byte and bits in output
                    outputByte = 0;
                    bitsInOutput = 8;
                }

                //If the bits in phrase is larger than 0
                //It means there are some bits remaining for processing
                if (bitsInPhrase > 0) {
                    //Processing phrase get masking with current phrase
                    processingPhrase = currentPhrase & (int) (Math.pow(2, bitsInPhrase) - 1);
                    //Do a or calculation to the final output byte
                    outputByte |= processingPhrase;
                    //For next time, we only consider a smaller size of bits in output
                    //Because the remaining bits will be shown at beginning
                    bitsInOutput -= bitsInPhrase;
                }

                //If the bits in phrase is 0
                //It means the remaining part is also 8 bits
                if (bitsInOutput == 0) {
                    //Simply out put the out put byte
                    System.out.write(outputByte);
					System.out.flush();
                    //Then reset for next input
                    outputByte = 0;
                    bitsInOutput = 8;
                }
                //Increase the entries in dictionary if wasnt reset char
				if(currentPhrase >= (maxEntries+1)){
					maxEntries =255;
				}
				else{
					maxEntries++;
				}

            }
			//Shift our output byte to left to give enough space for output of last byte
			outputByte <<= bitsInOutput;
			//Right shift current phrase number with the bits we need to pack
			processingPhrase = currentPhrase >> (bitsInPhrase - bitsInOutput);
			//Do a masking job
			processingPhrase &= (int) (Math.pow(2, bitsInOutput) - 1);
			//Reduce the bits size in current phrase for remaining bits which not processed here
			bitsInPhrase -= bitsInOutput;
			//Do a or calculation to the final output byte
			outputByte |= processingPhrase;
			//Write the output to system out
			System.out.write(outputByte);
			System.out.flush();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

    }
}
