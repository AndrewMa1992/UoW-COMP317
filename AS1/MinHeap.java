/**
 * @author Yunhao Fu
 * ID is 1255469
 * @version 0.9
 * <p>
 * Version Log:
 * 0.9 Added acknowledgements of references
 * 0.8 Fixed bugs of null pointer exception when extracting due to the current size pointer issue
 * 0.7 Commented methods of insertIntoDeadSpace(), activate() and hasMin()
 * 0.6 Added methods of insertIntoDeadSpace and activate to provide features of priority queue
 * 0.5 Fixed a indexing bug of last node pointer in insert method
 * 0.4 Changed the data type of node from integer to string
 * 0.3 Changed the public constructor to be private and using a static method to build a heap by the size or an integer array
 * 0.2 Debugged the up-heap and down-heap method to work correctly
 * 0.1 Built the framework of min heap
 * <p>
 * A classic min heap coded in Java with an array of Node to maintain features of min-heap.
 * An inner private class of Node is introduced to store value.
 * <p>
 * This is firstly used for COMP317-18A Assignment 1 as a supporting class of data structure.
 * <p>
 *     ACKNOWLEDGEMENT
 *     I, the author of this MinHeap file here by, great appreciate Tony C Smith's lectures about sorting.
 *     REFERENCE
 *     Weiss, M. A. (2010). Data structures & problem solving using Java (4th Edition). In Chapter 21 a priority queue: the binary heap (pp. 807-833). Boston, MA: Pearson.
 * </p>
 */
public class MinHeap {
    private Node[] heap; //An array to store the heap of data
    private int currentSize; //A pointer to point at the last index of node
    private final int CAP_SIZE; //The largest size of the min heap
    private int deadSpaceCounter; //Dead space counter

    /**
     * Private constructor of MinHeap
     * Passes a parameter of size of this min heap
     *
     * @param size The size of heap, for convenience index begins at 1. If the size passed in is 7, so the heap will be indexed as (1,2,3..7)
     */
    private MinHeap(int size) {
        this.CAP_SIZE = size;//The maximum size of the heap
        this.heap = new Node[CAP_SIZE + 1]; //The first node will be a placeholder, null, so add 1.
        this.currentSize = 0; //Index 0 node is for the placeholder node. So it starts at 1.
        this.deadSpaceCounter = 0;
    }

    /**
     * This is a static method to call for building a heap by size
     *
     * @param size How large the heap should be
     * @return The min heap data structure
     */
    public static MinHeap buildHeap(int size) {
        return new MinHeap(size);
    }

    /**
     * This is a static method to call for building a heap by integer array
     * It will insert all the integer number originally in array to heap
     *
     * @param integerArray The integer array which required to build heap
     * @return A built min heap
     */
    public static MinHeap buildHeap(String[] integerArray) {
        MinHeap temp = new MinHeap(integerArray.length);
        for (String integer : integerArray) {
            temp.insert(integer);
        }
        return temp;
    }

    /**
     * Inserts a value into min heap
     * It will automatically up heap the inserted value to suit the structure of min heap
     *
     * @param value The datum which need to be inserted
     */
    public void insert(String value) {
        //Check if the last node is null
        if (currentSize < heap.length) {
            //If it is null, it means this is the spare place
            //Initialize a node to the heap
            heap[++currentSize] = new Node(value);
            //Up heap the structure
            upHeap(currentSize);
        }
    }

    /**
     * Insert datum into the dead space when the new datum is less than the top
     *
     * @param deadSpaceInsert The datum which need to be inserted into dead space
     */
    public void insertIntoDeadSpace(String deadSpaceInsert) {
        if (currentSize + 1 < heap.length-deadSpaceCounter) {
            heap[currentSize + 1] = new Node(deadSpaceInsert);
            upHeap(currentSize);
            deadSpaceCounter++;
        }
    }

    /**
     * Activate dead spaces, to become a maximum size of heap
     */
    public void heapify() {
        //If dead space counter is zero, no need to heapify
        if (deadSpaceCounter == 0)
            return;
        //From the current size pointer to the CAP Size of heap
        for (int i = currentSize; i < CAP_SIZE; i++) {
            //Upheap though, re-build the heap
            upHeap(++currentSize);
        }
        //Resets the dead space
        deadSpaceCounter = 0;
    }

    /**
     * Extracts the node at index 1, which is the smallest one among the heap.
     * Firstly get the value of index 1 node, then make the last node come into front and do a down heap
     *
     * @return The smallest value at index 1
     */
    public String extract() {
        //Gets the value from the index 1 node
        String extractingValue = heap[1].getValue();
        //Makes the last one node come to be the first one
        heap[1] = heap[currentSize--];
        //Does a down heap form the top
        downHeap(1);
        //Return the extracting value
        return extractingValue;
    }

    /**
     * PrintMe is a supporting method to check the current status of heap.
     */
    public void printMe() {
        System.out.println("INDEX: VALUE");
        //Simply sequentially loop though the array of heap and print out.
        for (int i = 1; i <= currentSize; i++) {
            //Checks the null to avoid NPE
            if (heap[i] != null)
                System.out.println(i + ": " + heap[i].getValue());
        }
    }

    /**
     * To check if the heap has minimum value
     *
     * @return True, if the heap has minimum datum
     */
    public boolean hasMin() {
        return currentSize > 0;
    }

    /**
     * Does the down heap after removing the top value
     *
     * @param beginPoint The index where we start doing down heap, normally from the top
     */
    private void downHeap(int beginPoint) {
        int child;
        //While the double pointer is less than the CAP_SIZE, from the top to the bottom
        while (beginPoint * 2 <= currentSize) {
            //Checks the smaller child by calling findChild method with current index point
            child = findChild(beginPoint);
            //If it has child, checks the child with the parent which one is smaller
            if (heap[beginPoint].getValue().compareTo(heap[child].getValue()) > 0) {
                //The parent is bigger than the child, do a swap
                swap(beginPoint, child);
            }
            //Goes to next stage
            beginPoint = child;
        }
    }

    /**
     * This is a supporting private method to find which one child, right or left, is smaller.
     *
     * @param currentIndex The parent index
     * @return The smaller child index
     */
    private int findChild(int currentIndex) {
        //Double the currentIndex to locate the right child
        int child = 2 * currentIndex;
        //If the right child is the current size? Then just return.
        //If not, check both right and left child which one is smaller
        if (child != currentSize &&
                heap[child].getValue().compareTo(heap[child + 1].getValue()) > 0)
            //If the left child is smaller, then add 1 to child
            child++;
        //Return child value
        return child;
    }

    /**
     * Does the up heap after inserting a new value
     *
     * @param beginPoint The index where the new value inserted, normally the last position
     */
    private void upHeap(int beginPoint) {
        //While the index is greater than 0, from the bottom to the top
        while (beginPoint / 2 > 0) {
            //Checks the child and parent, which one is greater
            if (heap[beginPoint].getValue().compareTo(heap[beginPoint / 2].getValue()) < 0) {
                //If the parent is greater than the child, does a swap
                swap(beginPoint, beginPoint / 2);
            }
            //Goes to next stage
            beginPoint /= 2;
        }
    }

    /**
     * Another supporting method to swap two nodes in array
     *
     * @param from The "from" index of node
     * @param to   The "to" index of node
     */
    private void swap(int from, int to) {
        //Uses a temp value to hold the "to" node value
        String temp = heap[to].getValue();
        //Puts "from" to "to"
        heap[to].setValue(heap[from].getValue());
        //Recalls the "to" to "from"
        heap[from].setValue(temp);
    }


    /**
     * Inner class of node
     * It just contains one field of value
     * The reason using a node rather than simply int, is that it has capability to do further implements of new features.
     */
    private class Node {
        private String value;

        /**
         * Constructor of inner class of Node
         *
         * @param value The value of its node object
         */
        Node(String value) {
            this.value = value;
        }

        /**
         * Setter to mutate new value of the node
         *
         * @param value The new value
         */
        void setValue(String value) {
            this.value = value;
        }

        /**
         * Getter to access the value in the node
         *
         * @return Return the value
         */
        String getValue() {
            return value;
        }
    }
}