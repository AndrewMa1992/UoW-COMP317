// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * This class is for searching through a textfile for a RegExp
 * using a Finite State Machine outputing the lines that pass
 */
public class REsearch {
	FSM machine;
	
	/**
     * The constructor for REsearch
	 * @param filename : name of the file to search
     */   
	public REsearch(String filename){
		machine = new FSM();
		buildFSM();
		try{
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null){
				if(machine.tryText(line) == true)
					System.out.println(line);
			}
		}
		catch(Exception e){
			System.err.println(e.getMessage());
		}
	}
	
	/**
     * Main method
     * Expects a filename at runtime else returns
     */
	public static void main(String[] args){
		if (args.length != 1) {
            System.err.println("Usage: java REsearch <filename>");
            return;
        }
		REsearch res = new REsearch(args[0]);
	}
	
	/**
     * Method that creates a new FSM and then builds it
	 * based on the standard input 
     */   
	private void buildFSM(){
		Scanner scan = new Scanner(System.in);
		String line;
		int state, n1, n2;
		String character;
		machine = new FSM();
		while(scan.hasNext()){
			line = scan.nextLine();
			String[] input = line.split(" ");
			if(input.length == 4){
				state = Integer.parseInt(input[0]);
				character = input[1];
				n1 = Integer.parseInt(input[2]);
				n2 = Integer.parseInt(input[3]);
				machine.setState(state,character,n1,n2);
			}
		}
	}
}
