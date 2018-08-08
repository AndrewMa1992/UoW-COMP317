// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.util.Scanner;
import java.io.PrintStream;
import java.util.InputMismatchException;

/**
 * This class is for decoder of LZW
 */
class Decoder{
	/**
     * Main method for decoding by LZW algorithm
     *
     * @throws InputMismatchException if doesn't recieve integers from scanner
     */
	public static void main(String[] args){
		//Build a new trie
		ByteTrie trie = new ByteTrie();
		//Get the size of the trie
		int trieSize = trie.getSize();
		//Create input and output streams
		Scanner scan = new Scanner(System.in);
		PrintStream outStream = new PrintStream(System.out);
		//nextNode is the nextNode to get the bytes down to, for printing out
		int nextNode;
		byte[] buff;

		//Try/catch to prevent InputMismatch due to scanner
		try{
			while(scan.hasNext()){
				nextNode = scan.nextInt();
			
				if(nextNode >= trieSize){  
					//The requested node is outside of the trieSize, which is reset symbol
					trie = new ByteTrie();
					trieSize = trie.getSize();
				}
				else{
					//Add a blank node to trie and increase trie size
					trie.addBlank(trie.findIndex(nextNode));
					trieSize = trie.getSize();
					//Get bytes from the node for the index given
					buff = trie.getContent(trie.findIndex(nextNode));
					outStream.write(buff, 0, buff.length);
					outStream.flush();
				}
			} 
		}
		catch(InputMismatchException e){
			System.out.println("Error input mismatch! Int expected");
		}
	}
	
}