// Jason Hayman 1293913
// Yunhao Fu 1255469

import java.util.ArrayList;
import java.util.Arrays;

/**
 * REcompile file contains both parser and compiler *
 */
public class REcompile {
    //SPECIAL_CHARACTERS is a character array of special character which should be considered
    private static char[] SPECIAL_CHARACTERS = new char[]{'.', '*', '+', '?', '|', '(', ')', '[', ']', '!', '\\'};

    //expArray is an array of characters of regular expression
    private char[] expArray;
    //index of character in array
    private int index;
    //state no. of finite state machine
    private int state;
    //FSM indicates finite state machine
    private FSM fsm;
    //passedParsing is flag of regexp is in parsing(false) or compiling(true)
    private boolean passedParsing;
    //bracketLevel is to determine in which level of (nested) brackets
    private int bracketLevel;
    //escapeLevel is to determine the level of escape symbol \
    private int escapeLevel;
    private ArrayList<Integer> recentLeft;
    private int bracketLength;

    /**
     * Constructor of RE compiler
     *
     * @param exp string of regular expression
     */
    private REcompile(String exp) {
        //Initialise finite state machine
        fsm = new FSM();
        //put the exp string to char array
        this.expArray = exp.toCharArray();
        //initial value of index and state
        this.index = 0;
        this.state = 0;
        //set passedParsing to false
        passedParsing = false;
        bracketLevel = 0;
        escapeLevel=0;
        recentLeft=new ArrayList<Integer>();
        recentLeft.add(0);
        bracketLength=0;
    }

    /**
     * Main method of programme
     *
     * @param args length of args should be 1, referring a regexp
     */
    public static void main(String[] args) {
        //Check the length of args
        if (args.length != 1) {
            System.err.println("Error: One regular expression should be provided as argument.\nUsage: java REcompile <REGEXP>");
            return;
        }

        //Initialise the compiler object
        REcompile compiler = new REcompile(args[0]);

        //try-catch block to catch any exception
        try {
            //run the parse method to test the regexp is good or not
            compiler.parse();
            //check if it is passed parsing
            if (compiler.passedParsing) {
                //if so, compile it
                compiler.compile();
                //output all the state after compiling
                for (FSM.State s : compiler.fsm.getStates()) {
                    //Output to system standard out
                    System.out.println(s.toString());
                }
            } else {
                //Else, print error message
                System.err.println("Regular Expression seems not good.");
            }
        } catch (Exception e) {
            //Catch any exception
            System.err.println(e.getMessage());
            System.err.println(Arrays.toString(e.getStackTrace()));
        }

    }

    /**
     * parse method is to check if the regexp is good or not
     */
    private void parse() {
        //from top to bottom checking
        if (expression() == -1) {
            this.passedParsing = true;
        } else
            error();
    }

    /**
     * compile method is to build FSM for searcher
     */
    private void compile() {
        this.index = 0;
        this.state = 0;

        //Initial state at position 0, pointing to two 1, next state
        setState(this.state++, "NULL", 1, 1);

        try {
            //Go to do expression, from the top
            expression();
            //updateState(0, initial, initial);
            if ((index != expArray.length))
                error();
            //Set the last state pointing back to start
            setState(state, "END", 0, 0);
        } catch (Exception e) {
            //Catch any exception
            System.err.print(e.getMessage());
        }
    }

    /**
     * expression method, processing from the top to bottom
     * @return return index
     */
    private int expression() {
        if (passedParsing) {
            int r;
            //Go to branch first to test any disjunction
            r = branch();
            //If the pointer is at the rear
            if (index == this.expArray.length)
                //Just return
                return r;
            //Check current character
            if (isVocab(this.expArray[this.index])
                    || this.expArray[this.index] == '\\'
                    || this.expArray[this.index] == '.'
                    || this.expArray[this.index] == '['
                    || this.expArray[this.index] == '('
                    || ((this.expArray[this.index] == '!') && (this.expArray[this.index + 1] == '['))) {
                //These above need to do expression once more
                expression();
                //ignoreLevel is for escape
                if (escapeLevel != 0)
                    //reduce escape level
                    escapeLevel--;
                //If the pointer is at the rear
                if (index == this.expArray.length)
                    //Just return
                    return r;
            }
            //if bracket level is greater than 0, it is in nested expression
            if (bracketLevel > 0)
                //Just return r
                return r;
            else
                //else output error()
                error();
            //return r
            return r;
        } else {
            //Parser part
            //if the index is equal to length
            if (index == expArray.length)
                //return -1, ok signal
                return -1;
            //do branch checking
            branch();
            //get back from branch(), check index again
            if (index == expArray.length)
                return -1;
            //check symbol for next stage
            if (isVocab(this.expArray[this.index])
                    || this.expArray[this.index] == '['
                    || this.expArray[this.index] == '('
                    || ((this.expArray[this.index] == '!') && this.expArray[this.index + 1] == '[')
                    || this.expArray[this.index] == '\\'
                    || this.expArray[this.index] == '.')
                expression();
            //ending index check
            if (index == expArray.length)
                return -1;
            //check bracket level
            if (bracketLevel > 0)
                return -1;
            //show error
            else error();
            //return -1
            return -1;
        }
    }

    /**
     * branch method is to check any disjunction, the |
     * @return return index
     */
    private int branch() {
        if (passedParsing) {
            //r is for return, start is for right start side of disjunction, braStart is for left start side of disjunction
            int r, start, end, braStart, braEnd;
            //process next stage of term
            start = term();
            //get the right ending index
            end = state - 1;
            //return value
            r = start;

            //ending index check
            if (index == expArray.length)
                return r;

            //check the branch symbol |
            if (expArray[this.index] == '|') {
                //increase index for symbol |
                index++;
                //check if the left part of branch is nested by bracket
                if(expArray[index]!='('&&expArray[index]!='!'&&expArray[index]!='[') {
                    //if not, take next character in account
                    setState(state, String.valueOf(expArray[index]), state + 1, state + 1);
                    //increase index
                    index++;
                    //new return value
                    r = start;
                    //set braStart
                    braStart = state;
                    state++;
                    //get braEnd
                    braEnd = term() - 1;
                    //set the final joint state machine
                    setState(state, "NULL", state + 1, state + 1);
                    state++;
                    //update branch states
                    updateState(end, braEnd + 1, braEnd + 1);
                    updateState(braEnd, braEnd + 1, braEnd + 1);
                    updateState(recentLeft.get(recentLeft.size()-1),
                            recentLeft.get(recentLeft.size()-1)+1,
                            braStart);
                }
                else {
                    //if so, do factor further
                    r = factor();
                    //update branch states
                    updateState((start-2)<0?(start-1):(start-2),start-1,r-1);
                    updateState(end,state,state);
                }
            }
            //return r
            return r;
        } else {
            //Parser checks the | symbol
            if (index == expArray.length)
                return -1;
            term();
            if (index == expArray.length)
                return -1;
            if (expArray[this.index] == '|') {
                index++;
                branch();
            }
            return -1;
        }
    }

    /**
     * list() method is to solve [] and ![]! where contains all literal without any special meaning
     */
    private void list(String begin) {
        String bracket = begin;
        if (passedParsing) {
            StringBuilder items = new StringBuilder();
            items.append(bracket);
            //checking the ending of bracket
            while (expArray[index] != ']') {
                items.append(expArray[index]);
                index++;
            }
            setState(state, items.toString(),state+1,state+1);
            state++;
        } else {
            //For parser, checking the ending symbol of ]
            try {
                if (this.expArray[index] != ']') {
                    if (index <= expArray.length) {
                        index++;
                        list(bracket);
                    } else error();
                }
            } catch (IndexOutOfBoundsException ibe) {
                error();
            }
        }
    }

    /**
     * term() method is to check term
     * @return
     */
    private int term() {
        if (passedParsing) {
            int r, t, f;
            f = this.state-1;
            r = t = factor();

            //check ending index
            if (index == this.expArray.length)
                return r;
            //check the * symbol
            if (this.expArray[this.index] == '*') {
                //set state
                // TODO * should put one next to previous looping state and one next to the next state
                setState(this.state, "NULL", this.state + 1, t-escapeLevel);

                //update state
                updateState(t-2>0?t-2:t-1,
                        t-escapeLevel, state+1);

                //set return
                r = state;
                //increase index and state
                this.index++;
                this.state++;
            } else if (this.expArray[this.index] == '+') { //check the + symbol
                this.index++;
                r = state - 1;
                //set state
                setState(state, "NULL", t, state + 1);
                state++;
            } else if (this.expArray[index] == '?') { // check the ? symbol
                //increase index
                index++;
                //set state
                setState(state, "NULL", state + 1, state + 1);
                //update state
                //TODO How to get the initial index
                updateState(t-2>0?t-2:t-1,t, state+1);
                //set return value
                r = state;
                //increase state
                state++;
            } else if (expArray[index] == '.') {
                setState(state, "..", state + 1, state + 1);
                state++;
                index++;
            }
            return r;
        } else {
            //For parser
            //check ending index
            if (index == expArray.length)
                return -1;
            //do factor
            factor();
            //check ending index again
            if (index == expArray.length)
                return -1;
            //check different symbols, *, ?, +
            if (this.expArray[this.index] == '*')
                this.index++;
            else if (this.expArray[this.index] == '?')
                this.index++;
            else if (this.expArray[this.index] == '+') {
                this.index++;
            }
            //check ending index again
            if (index == expArray.length)
                return -1;
            //check wild symbol
            if (this.expArray[this.index] == '.')
                this.index++;
            return -1;
        }
    }

    /**
     * factor() is to check factor
     * @return index
     */
    private int factor() {
        if (passedParsing) {
            //check following factor
            int r = state;
            if (index == expArray.length)
                return r;
            //\\ checking
            if (this.expArray[index] == '\\') {
                //increase escape level
                escapeLevel++;
                //increase index
                index++;
                //set state
                setState(state, String.valueOf(expArray[index]), state + 1, state + 1);
                //set return r
                r = state;
                //increase state
                state++;
                //increase index
                index++;
            } else if (isVocab(this.expArray[index])) { // check if it is vocab
                //set state
                setState(state, String.valueOf(this.expArray[index]), state + 1, state + 1);
                //increase index
                index++;
                //set return
                r = state;
                //increase state
                state++;
            } else if (this.expArray[index] == '!') { //check ! symbol
                index++;
                //check [
                if (this.expArray[index] == '[') {
                    //increase bracket level
                    bracketLevel++;
                    //set state
                    //setState(state, "![", state + 1, state + 1);
                    //increase state and index
                    //state++;
                    index++;
                    //do list()
                    list("![");
                    //check ending bracket
                    if (this.expArray[index] == ']') {
                        //set state
                        setState(state, "NULL", state + 1, state + 1);
                        //increase state and index
                        state++;
                        index++;
                        //decrease bracket level
                        bracketLevel--;
                    } else error();
                } else error();
                if (this.expArray[index] == '!') {//check the ending !
                    index++;
                } else error();
            } else if (this.expArray[index] == '[') { // check simple [
                //increase bracket level
                bracketLevel++;
                //set state
                //setState(state, "[", state + 1, state + 1);
                //increase state and index
                //state++;
                index++;
                //do list()
                list("[");
                if (this.expArray[index] == ']') { // check ending ]
                    //set state
                    setState(state, "NULL", state + 1, state + 1);
                    //increase state and index
                    state++;
                    index++;
                    //decrease bracket level
                    bracketLevel--;
                } else error();
            } else if (this.expArray[index] == '(') { //check (
                recentLeft.add(state);
                bracketLength=index;
                //increase bracket level
                bracketLevel++;
                //set state
                setState(state, "NULL", state + 1, state + 1);
                //increase state and index
                state++;
                index++;
                //do expression
                r = expression();
                if (expArray[index] == ')') {//check )
                    bracketLength = index-bracketLength;
                    recentLeft.remove(recentLeft.size()-1);
                    //reduce bracket level
                    bracketLevel--;
                    setState(state, "NULL", state + 1, state + 1);
                    //increase state and index
                    index++;
                    state++;
                } else error();
            }
            return r;
        } else {
            //For parser
            //check ending index
            if (index == expArray.length)
                return -1;
            //check is vocab, if so, increase index
            if (isVocab(this.expArray[this.index])) this.index++;
            else if (this.expArray[this.index] == '(') {//check (
                //increase bracket level
                bracketLevel++;
                //increase index
                this.index++;
                //do expression
                expression();
                //check ending index when get back from expression
                if (index >= expArray.length) error();
                //check ending )
                if (this.expArray[this.index] == ')') {
                    //increase the index and reduce bracket level
                    this.index++;
                    bracketLevel--;
                } else error();
            } else if (this.expArray[this.index] == '!') {// check !
                //increase index
                this.index++;
                if (this.expArray[this.index] == '[') { // check [
                    //increase bracket level
                    bracketLevel++;
                    //do the list
                    list("![");
                    //check ending index when get back from list
                    if (index >= expArray.length) error();
                    //check ending ] symbol
                    if (this.expArray[this.index] == ']') {
                        //increase index and reduce bracket level
                        this.index++;
                        bracketLevel--;
                    } else error();
                } else error();
                //check ending !
                if (this.expArray[this.index] == '!') this.index++;
                else error();
            } else if (this.expArray[this.index] == '[') {//check simple [
                //increase bracket level and index
                bracketLevel++;
                this.index++;
                //do list
                list("[");
                //check ending index when get back from list()
                if (index >= expArray.length) error();
                //check ending ]
                if (this.expArray[this.index] == ']') {
                    //increase index and reduce bracket level
                    this.index++;
                    bracketLevel--;
                } else error();
            } else if (expArray[index] == '\\') {//check \
                //while next symbol is \\
                while (expArray[index++] == '\\') {
                    //increase index
                    index++;
                    //if it is at rear
                    if (index == this.expArray.length)
                        //break
                        break;
                }
            } else if(expArray[index]=='.'){
                index++;
            }else error();
        }
        return -1;
    }

    /**
     * set state method to add a new state to FSM, finite state machine
     * @param state state number
     * @param ch string of character
     * @param n1 next state number
     * @param n2 another next state number
     */
    private void setState(int state, String ch, int n1, int n2) {
        fsm.setState(state, ch, n1, n2);
    }

    /**
     * update state is to update a certain state
     * @param state state number
     * @param n1 new next state number
     * @param n2 another new next state number
     */
    private void updateState(int state, int n1, int n2) {
        fsm.updateState(state, n1, n2);
    }

    /**
     * isVocab is to check special characters
     * @param c character for checking
     * @return true if it is vocabulary
     */
    private boolean isVocab(char c) {
        for (char c1 : SPECIAL_CHARACTERS)
            if (c1 == c) return false;
        return true;
    }

    /**
     * error message and throw exception to stop
     */
    private void error() {
        throw new IllegalArgumentException(String.format("%s is not valid regular expression, please check.", Arrays.toString(expArray)));
    }
}
