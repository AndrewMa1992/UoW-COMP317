import java.io.*;
import java.util.Scanner;

/**
 * @author Yunhao Fu
 * ID is 1255469
 * @version 0.9
 * <p>
 * Version Log:
 * 0.9 Passed tests with n=3200, 6400, 12800 and k=3, 7, 15, 31, 17, 56, 97, the result is below, each n is 16 bits long
 *     k    3       7       31      17      56      97
 *     n    3200    6400    12800   3200    6400    12800
 *     runs 532     456     207     96      58      67
 *     avg  533.33  457.14  206.45  94.12   57.14   65.98   (as calculated by n/2k)
 *     time 94      125     172     94      110     157     (in million seconds)
 *     As the result table above, the MakeRuns really did a good job to make runs
 *     since the amount of runs are around the theoretical average number.
 * 0.8 Rolled back using new version of MinHeap
 * 0.7 Fixed bugs of outputting empty runs
 * 0.6 Used more general Heap class not MinHeap class
 * 0.5 Stopped using Run class but directly output to file for saving memory
 * 0.4 Added all the comments in MakeRuns.java
 * 0.3 Updated to use replacement selection to make runs more efficiently. (Where the amount of runs is decreased to around n/2k, k is the size of heap)
 * 0.2 Developed the first extract/insert processing for making runs. (Where the amount of runs is generally n/2)
 * 0.1 Built basic framework of this class. i.e. the arguments length check and try-catch block
 * <p>
 * MakeRuns class is used for making runs from what inputted and output the runs
 * It is based on a min heap (priority queue) and replacement selection.
 */
public class MakeRuns {
    private static int MAX_SIZE; //MAX_SIZE is variable to save the size of heap
    private static File file; //The input file name

    /**
     * Main executable method to process runs
     *
     * @param args The length of args should be 2, first argument is the size of heap where second one is the input file containing data
     */
    public static void main(String[] args) {
        //Checks the length of arguments, if it is not 2, return with a usage message by error print
        if (args.length != 2) {
            System.err.println("Error: Arguments are not matched!" +
                    "\nUsage: java MakeRuns k filename" +
                    "\nWhere the k is the maximum size of heap and filename is the file waiting for sort.");
            return;
        }

        //Initialises a null scanner to read file
        Scanner sc = null;
        //Try-catch block to handle different exception
        try {

            //Takes the size from argument array
            MAX_SIZE = Integer.parseInt(args[0]);
            //Takes the file name and makes a File object
            file = new File(args[1]);
            //Checks the file if it exists or not
            if (file.exists()) {
                //If it exist, initialises the scanner with the file
                sc = new Scanner(file);

                File runs = new File(file.getAbsolutePath() + ".runs");
                FileWriter writer = new FileWriter(runs);
                int runsCounter = 0;

                //Constructs the first array of data waiting to input to memory
                String[] tempArray = new String[MAX_SIZE];
                //Sequentially loops though the size of data
                for (int i = 0; i < MAX_SIZE; i++) {
                    //Puts the datum to array
                    tempArray[i] = sc.nextLine();
                }
                //Calls the static method in MinHeap to build a heap based on the array
                MinHeap minHeap = MinHeap.buildHeap(tempArray);
                //Cleans the temp array to free the memory
                tempArray = null;

                //Creates a tempMin String object to use as a flag
                String tempMin = "";
                //Creates a tempInsert object to use as a inserting check
                String tempInsert = "";
                String lastAppend = "";

                while (sc.hasNext()) {
                    //Gets the minimum datum from the top of heap by calling extract() method
                    tempMin = minHeap.extract();
                    //Compares with the last item in run
                    if (lastAppend.compareTo(tempMin) < 0) {
                        //If the temp Min is greater than the last item in run
                        //Then adds it to run
                        writer.append(tempMin).append("\n");
                    } else {
                        writer.append("$$\n").append(tempMin).append("\n");
                        runsCounter++;
                    }

                    lastAppend=tempMin;

                    //Gets the next datum from scanner
                    tempInsert = sc.nextLine();
                    //Checks if the new inserting datum is greater than the run maximum
                    if (lastAppend.compareTo(tempInsert) < 0) {
                        //If so, insert into heap
                        minHeap.insert(tempInsert);
                    } else if (minHeap.hasMin()) {
                        //If not, checks if the heap has its minimum value
                        //If so, insert the datum into dead space
                        //The term of Dead Space can be referred to
                        //Weiss, M. A. (2010). Data structures & problem solving using Java (4th Edition). In 21.6.6 replacement selection (pp. 832-833). Boston, MA: Pearson.
                        minHeap.insertIntoDeadSpace(tempInsert);
                    } else {
                        minHeap.insertIntoDeadSpace(tempInsert);
                        //If once there are all dead spaces in heap, calls the activate method
                        minHeap.heapify();
                    }
                }

                //If the above while loop finished means that there is no more input datum
                //Calls the activate method once again
                minHeap.heapify();
                //While the heap has minimum datum
                while (minHeap.hasMin()) {
                    //Gets the minimum datum from the top of heap by calling extract() method
                    tempMin = minHeap.extract();
                    //Compares with the last item in run
                    if (lastAppend.compareTo(tempMin) < 0) {
                        //If the temp Min is greater than the last item in run
                        //Then adds it to run
                        writer.append(tempMin).append("\n");
                    } else {
                        writer.append("$$\n").append(tempMin).append("\n");
                        runsCounter++;
                    }
                    lastAppend=tempMin;
                }
                writer.append("$$");
                runsCounter++;
                writer.close();
                //Prints how many runs it created
                System.err.println("Amount of Runs: " + runsCounter);
            } else {
                //If the file does not exist, prints the error message
                System.err.println("Error: The file does not exist!");
            }
        } catch (NumberFormatException e) {
            //If the K value cannot convert to integer, prints the error
            System.err.println("Error: K value cannot convert to integer!" +
                    "\nPlease check your k and filename arguments");
        } catch (IOException e) {
            System.err.println("Error: IO exception when writing file!");
        }catch (Exception e){
            System.err.println("Error: General Exception Occurred!");
        }
    }

}
