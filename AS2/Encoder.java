// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

/**
 * This class is for encoder of LZW
 */
public class Encoder {
    /**
     * Main method for encoding by LZW algorithm
     *
     * @param args one argument for setting the max size of trie
     * @throws IOException IO exception will be thrown if IO stream meets error
     */
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.err.println("Usage: java Encoder <integer MAX_SIZE>");
            return;
        }

        int maxSize = Integer.parseInt(args[0]);
        if (maxSize <= 256) {
            System.err.println("MAX SIZE should be greater than 256");
            return;
        }

        try {


            //Initialise the byte trie for LZW encoding
            ByteTrie byteTrie = new ByteTrie();
            //Use scanner to read from system.in
            Scanner scanner = new Scanner(System.in);

            //Use a byte array input stream to manipulate bytes
            ByteArrayInputStream inputStream = new ByteArrayInputStream(scanner.next().getBytes());
            //Use a Byte array list to maintain the encoding bytes from system in
            ArrayList<Byte> encodingBytes = new ArrayList<Byte>();
            //Get the parent trie node for insert child
            ByteTrie.TrieNode parentTrieNode = byteTrie.getRoot();
            //The last byte encoding for each time
            Byte lastByteEncoding = null;

            //Add the first byte into encoding byte list
            encodingBytes.add((byte) inputStream.read());

            //While the input stream is available
            while (inputStream.available() > 0) {
                if (maxSize > byteTrie.getSize()) {
                    //While we could get a child from the byte trie while checking the last item in list
                    while (byteTrie.getChild(encodingBytes.get(encodingBytes.size() - 1), parentTrieNode) != null) {
                        //If so, go to next step
                        parentTrieNode = byteTrie.getChild(encodingBytes.get(encodingBytes.size() - 1), parentTrieNode);
                        //Read one more to encoding bytes
                        encodingBytes.add((byte) inputStream.read());
                    }
                    //If the above while loop is break, check again and set the parent trie node to the right place
                    if (byteTrie.getChild(encodingBytes.get(encodingBytes.size() - 1), parentTrieNode) != null) {
                        parentTrieNode = byteTrie.getChild(encodingBytes.get(encodingBytes.size() - 1), parentTrieNode);
                    }
                    //Add the byte to byte trie
                    byteTrie.add(encodingBytes.get(encodingBytes.size() - 1), parentTrieNode);
                    //Out put to the system out
                    System.out.println(byteTrie.findNode(parentTrieNode));
                    //Get the last byte we are encoding
                    lastByteEncoding = encodingBytes.get(encodingBytes.size() - 1);
                    //Reset the encoding byte list
                    encodingBytes = new ArrayList<Byte>();
                    //Add the last byte into encoding bytes
                    encodingBytes.add(lastByteEncoding);
                    //Reset the parent trie to the root
                    parentTrieNode = byteTrie.getRoot();
                } else {
                    System.out.println(2 * byteTrie.getSize());
                    byteTrie = new ByteTrie();
					parentTrieNode = byteTrie.getRoot();
                }
            }

            //Output the index of last item in encoding bytes
            if (encodingBytes.get(0) != (byte) -1) {
				if(maxSize >= byteTrie.getSize()){
					System.out.println(2 * byteTrie.getSize());
                    byteTrie = new ByteTrie();
					parentTrieNode = byteTrie.getRoot();
				}
                System.out.println(byteTrie.findNode(byteTrie.getChild(encodingBytes.get(encodingBytes.size() - 1), parentTrieNode)));
            }
        } catch (Exception e) {
            System.out.println("General exception: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }
}
