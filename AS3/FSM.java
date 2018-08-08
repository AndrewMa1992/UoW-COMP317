// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.util.ArrayList;

/**
 * This class is for building a Finite state machine
 * which can be passed a line of text and returns if it passes or fails
 */
public class FSM {
	ArrayList<State> states;
	FSMDeque deque;
	String text;
	
	public FSM(){
		deque = new FSMDeque();
		states = new ArrayList<State>();
	}
	
	/**
     * The method for running the FSM on a given text
	 * @param text		: the string of text to run the machine on
     * @return Boolean of whether the machine completed
     */   
	public boolean tryText(String text){
		this.text = text;
		int startChar = 0;
		//Run the machine starting from each char in the text until it passes or reaches the end
		while(startChar < text.length()){  
			if(runExpression(startChar) == true)	
				return true;
			startChar++;
		}
		return false;
	}
	
	/**
     * The private method for testing a string from a position in the string
	 * @param position		: the char in the text to start from
     * @return boolean true if the string passed false otherwise
     */   
	private boolean runExpression(int position){
		deque = new FSMDeque();
		State currentState = states.get(0);
		//Get the first state(s) to add to the deque from the start state
		if(currentState.acceptingChar.equals("NULL")){
			deque.addFront(states.get(currentState.next1));
			if(states.get(currentState.next1) != states.get(currentState.next2))
				deque.addFront(states.get(currentState.next2));
		}
		//Loop while there is more than just the scan in the deque and the position hasn't reached end
		while(deque.size() > 1 && position < text.length()){
		currentState = deque.getFromFront();
			
			while(currentState != null){
				//Goal state
				if(currentState.acceptingChar.equals("END"))
					return true;
				//Branch node
				if(currentState.acceptingChar.equals("NULL")){
					deque.addFront(states.get(currentState.next1));
					if(states.get(currentState.next1) != states.get(currentState.next2))
						deque.addFront(states.get(currentState.next2));
				}
				//Two-state
				else if(currentState.accepts(text.charAt(position))){
					deque.addRear(states.get(currentState.next1));
				}
				currentState = deque.getFromFront();
			}
			position++;
			deque.addRearScan();
		}
		//Check if is at final state or can reach without consuming chars
		while(deque.size() > 1){
			currentState = deque.getFromFront();
			while(currentState != null){
				//Goal state
				if(currentState.acceptingChar.equals("END"))
					return true;
				//Branch state
				if(currentState.acceptingChar.equals("NULL")){
					deque.addFront(states.get(currentState.next1));
					if(states.get(currentState.next1) != states.get(currentState.next2))
						deque.addFront(states.get(currentState.next2));
				}
				currentState = deque.getFromFront();
			}
			deque.addRearScan();
		}	
		return false;
	}
		
	/**
     * The method for building the FSM by setting each state individuall
	 * @param state 		: the state number
	 * @param character		: the char the state wants
	 * @param n1			: the first option for next state
	 * @param n2			: the second option for next state
     */   
	public void setState(int state, String character, int n1, int n2){  
		states.add(new State(state, character, n1, n2));
	}

	public void updateState(int state, int n1, int n2){
		State temp = states.remove(state);

		if(n1 == n2){
			if(temp.next1 == temp.next2){
				temp.next1=n1;
				temp.next2=n2;
			}else{
				temp.next2=n1;
			}
		}else{
			temp.next1=n1;
			temp.next2=n2;
		}

		states.add(state,temp);
	}

	/**
     * The method for getting a state represented as a string
	 * @param state 	: number of the state to return
     * @return a state represented as a string
     */   
	public String getState(int state){
		return states.get(state).toString();
	}

	public ArrayList<State> getStates(){return states;}

	/**
	* This class is for containing a data type State
	*/
	class State{
		int stateNum;
		String acceptingChar;
		int next1;
		int next2;
		
		/**
		* State constructor
		* @param stateNum 			: the state number
		* @param acceptingChar		: the char the state wants
		* @param next1				: the first option for next state
		* @param next2				: the second option for next state
		*/
		State(int stateNum, String acceptingChar, int next1, int next2){
			this.stateNum = stateNum;
			this.acceptingChar = acceptingChar;
			this.next1 = next1;
			this.next2 = next2;
		}
		
		/**
		* Method to test if a char is accepted by a state or not
		* @param toAccept 	: the char to test if this state accepts
		* @return boolean if it passed or not
		*/
		boolean accepts(char toAccept){
			if(!acceptingChar.equals("NULL")){
				//Two-state single accepting char
				if(acceptingChar.length() == 1){
					return (acceptingChar.charAt(0) == toAccept);
				}
				//Two-state accept if one of eg. [abc
				else if(acceptingChar.charAt(0) == '['){
					for(int i = 1; i < acceptingChar.length(); i++){
						if(acceptingChar.charAt(i) == toAccept)
							return true;
					}
				}
				//Two-state accept if not one of eg. ![abc
				else if(acceptingChar.charAt(0) == '!' && 
						acceptingChar.charAt(1) == '['){
					for(int i = 2; i < acceptingChar.length(); i++){
						if(acceptingChar.charAt(i) != toAccept)
							return true;
					}	
				}
				//Two-state accept any single char 
				else if(acceptingChar.charAt(0) == '.' &&
						acceptingChar.charAt(1) == '.'){
					return true;
				}
			}
			return false;
		}
		
		/**
		* toString implimentation for a state
		* @return the state as a string
		*/
		public String toString(){
			return stateNum + " " + acceptingChar + " " + next1 + " " + next2;
		}
	}
	
	/**
	* This class is for nodes to build a FSMDeque 
	*/
    class StateNode {
        StateNode beforeThis;
        StateNode afterThis;
        State state;

		/**
		* StateNode constructor
		* @param beforeThis : the StateNode that is before this node in the deque
		* @param afterThis	: the StateNode that is after this node in the deque
		* @param data		: the state this StateNode contains
		*/
        StateNode(StateNode beforeThis, StateNode afterThis, State data) {
            state = data;
            this.beforeThis = beforeThis;
            this.afterThis = afterThis;
        }
		
		/**
		* StateNode constructor for a scan node
		* @param beforeThis : the StateNode that is before this node in the deque
		* @param afterThis	: the StateNode that is after this node in the deque
		*/
		StateNode(StateNode beforeThis, StateNode afterThis){
			this.beforeThis = beforeThis;
            this.afterThis = afterThis;
			state = null;
		}
    }
   
	/**
	* This class is maintainig a Deque for FSM's
	*/
	class FSMDeque {
		private StateNode front;
		private StateNode rear;
		private int size;

		/**
		* FSMDeque constructor
		*/
		public FSMDeque() {
			front = rear = new StateNode(null, null);
			size = 1;
		}
	
		/**
		* Method to add a state to the front of the FSMDeque
		* @param data 	: the state to be added
		*/
		public void addFront(State data) {	
			if(front != null){
				StateNode temp = new StateNode(null, front, data);
				front.beforeThis = temp;
				front = temp;
			}
			else{
				StateNode temp = new StateNode(null, null, data);
				rear = temp;
				front = temp;
			}
			size++;
		}
	
		/**
		* Method to add a state to the rear of the FSMDeque
		* @param data 	: the state to be added
		*/
		public void addRear(State data) {
			if(rear != null){
				StateNode temp = new StateNode(rear, null, data);
				rear.afterThis = temp;
				rear = temp;
			}
			else{
				StateNode temp = new StateNode(null, null, data);
				rear = temp;
				front = temp;
			}	
			size++;
		}
		
		/**
		* Method to add a scan to the rear of the FSMDeque
		*/
		public void addRearScan(){
			if(rear != null){
			StateNode temp = new StateNode(rear, null);
			rear.afterThis = temp;
			rear = temp;
			}
			else{
				StateNode temp = new StateNode(null, null);
				rear = temp;
				front = temp;
			}
			size++;
		}
		
		/**
		* Method to get the current size of the FSMDeque
		* @return the size 
		*/
		public int size() {
			return size;
		}

		/**
		* Method to get the state from the front of the FSMDeque and remove it from the FSMDeque
		* @return the state of the current front node 
		*/
		public State getFromFront() {
			StateNode temp = front;
			front = front.afterThis;
			if(front != null) front.beforeThis = null;
			size--;
			return temp.state;
		}

		/**
		* Method to get the state from the rear of the FSMDeque and remove it from the FSMDeque
		* @return the state of the current rear node 
		*/
		public State getFromRear() {
			StateNode temp = rear;
			rear = rear.beforeThis;
			if(rear != null) rear.afterThis = null;
			size--;
			return temp.state;

		}
		
		//test method to check what deque looks like
		public String toString(){
			String toReturn = "";
			StateNode curr = front;
			while(curr != null){
				if(curr.state != null)toReturn = toReturn + " " + curr.state.stateNum;
				else toReturn = toReturn + " scan";
				curr = curr.afterThis;
			}
		return toReturn;
		}
		
	}
}