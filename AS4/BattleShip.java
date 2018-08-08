import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class BattleShip {
    private static int[] battleShips;
    private static int[] xHits;
    private static int[] yHits;
    private static Game[] solutions;
    private static int resourceLimit;
    private static Game bestRecord;

    public static void main(String[] args) throws FileNotFoundException {
        //Check arguments length
        if (args.length != 2) {
            System.err.println("Usage: java BattleShip <FILE> <DEFINE_MAX_RESOURCE_NUMBER>");
            return;
        }


        //set arguments settings into programme
        try {
            resourceLimit = Integer.parseInt(args[1]);
        } catch (Exception e) {
            System.err.println("Error: Cannot parse second argument to an integer!");
        }
        solutions = new Game[resourceLimit];

        //Read input file
        Scanner sc = new Scanner(new File(args[0]));
        while (sc.hasNext()) {
            String temp = sc.nextLine();
            String[] tempArray = temp.split(" ");

            if (battleShips == null) {
                battleShips = new int[tempArray.length];
                for (int i = 0; i < tempArray.length; i++) {
                    battleShips[i] = Integer.parseInt(tempArray[i]);
                }
            } else if (xHits == null) {
                xHits = new int[tempArray.length];
                for (int i = 0; i < tempArray.length; i++) {
                    xHits[i] = Integer.parseInt(tempArray[i]);
                }
            } else if (yHits == null) {
                yHits = new int[tempArray.length];
                for (int i = 0; i < tempArray.length; i++) {
                    yHits[i] = Integer.parseInt(tempArray[i]);
                }
            } else {
                System.err.println("Failed to read file, please check");
                return;
            }
        }
        System.out.println("Game Info:\nBattle Ships: " + Arrays.toString(battleShips));
        System.out.println("xHits: " + Arrays.toString(xHits));
        System.out.println("yHits: " + Arrays.toString(yHits));

        //Heuristic Algorithm begins
        //Genetic Algorithm Concepts:
        //Breed a population of games
        System.out.println("\nStep 1: Massively generating " + resourceLimit + " numbers of solutions...");
        solutions = generate(resourceLimit);

        //Verify each game item
        for (Game g : solutions) {
            verify(g);
        }
        //Sort all solutions by its score increasingly
        Arrays.sort(solutions);
        bestRecord = solutions[0].copy();
        //Test if we currently have an optimal solution
        if (bestRecord.getScores() == 0) {
            //If so, output the solution and return
            System.out.println("Lucky! One of Optimal Solutions with Score 0 Found as: \n " + bestRecord.toString());
            return;
        }

        //If not, output current best results to user
        int counter = 0;
        for (Game g : solutions) {
            if (g.getScores() == bestRecord.getScores())
                counter++;
            if (g.getScores() > bestRecord.getScores())
                break;
        }
        //Output current best solution with its score
        System.out.println("\nStep 2: All solutions got verified and sorted! Currently the lowest score is " + bestRecord.getScores() + ". \n" +
                "There is(are) " + counter + " set(s) of solution having same score.\n\n" +
                "**Now GA starts breeding, mutating and killing while within resource limit.**\n");

        //GA starts its genetic operation
        while (resourceLimit > 0) {
            //Do GA algorithm
            genetic(solutions);
            System.out.println("Applying genetic algorithm...Please Wait...");
            //Verify all solutions again
            for (Game g : solutions) {
                verify(g);
            }
            //Sort them out
            Arrays.sort(solutions);
            if (bestRecord.getScores() > solutions[0].getScores())
                bestRecord = solutions[0].copy();
            if (bestRecord.getScores() == 0) {
                System.out.println("\nLucky! One of Optimal Solutions with Score 0 Found as: \n " + bestRecord.toString());
                break;
            }
        }
        //Verify all solutions again
        for (Game g : solutions) {
            verify(g);
        }
        //When it reaches resource limit, output the current best one
        if (resourceLimit <= 0) {
            System.out.println("\nSorry! Run out of resource! But current best solution is: " + bestRecord.toString() + "\n" +
                    "SCORE:" + bestRecord.getScores());
        }
    }

    /**
     * Main function of genetic algorithm - GA
     * It basically does FOUR things:
     * **********************************************
     * Selection -> Breeding -> Mutation -> Killing
     * **********************************************
     * @param solutions A population of games solution
     */
    private static void genetic(Game[] solutions) {
        //===========================================
        //SELECTION - Selection two mates to breed new offspring
        //===========================================
        //New offspring
        Game temp = new Game(xHits.length, yHits.length);
        //Get first and second best games
        int[][] temp1 = new int[xHits.length][yHits.length];
        for (int i = 0; i < xHits.length * yHits.length; i++) {
            int r = i / yHits.length;
            int c = i % xHits.length;
            temp1[r][c] = solutions[0].getGrid()[r][c];
        }
        int[][] temp2 = new int[xHits.length][yHits.length];
        for (int i = 0; i < xHits.length * yHits.length; i++) {
            int r = i / yHits.length;
            int c = i % xHits.length;
            temp2[r][c] = solutions[1].getGrid()[r][c];
        }


        //To get genes from solutions
        Gene[] temp1Gene = new Gene[battleShips.length];
        Gene[] temp2Gene = new Gene[battleShips.length];
        //Initialise each gene
        for (int i = 0; i < battleShips.length; i++) {
            temp1Gene[i] = new Gene(battleShips[i]);
            temp2Gene[i] = new Gene(battleShips[i]);
        }
        //Add ship position into genes
        for (int i = 0; i < battleShips.length; i++) {
            int length = battleShips[i];
            for (int j = 0; j < xHits.length * yHits.length; j++) {
                int r = j / yHits.length;
                int c = j % xHits.length;
                if (temp1[r][c] == battleShips[i]) {
                    temp1Gene[i].addPos(r, c);
                    temp1[r][c] = 0;
                    length--;
                }
                if (length <= 0)
                    break;
            }
        }
        for (int i = 0; i < battleShips.length; i++) {
            int length = battleShips[i];
            for (int j = 0; j < xHits.length * yHits.length; j++) {
                int r = j / yHits.length;
                int c = j % xHits.length;
                if (temp2[r][c] == battleShips[i]) {
                    temp2Gene[i].addPos(r, c);
                    temp2[r][c] = 0;
                    length--;
                }
                if (length <= 0)
                    break;
            }
        }


        //===========================================
        //CROSSOVER - Breed new gene by two genes selected above with crossover method
        //===========================================
        Gene[] tempGene = new Gene[battleShips.length];
        crossover:
        while (resourceLimit > 0) {
            tempGene = new Gene[battleShips.length];
            //Crossover two serials of genes to generate new
            for (int i = 0; i < tempGene.length; i++) {
                resourceLimit--;
                int random = ThreadLocalRandom.current().nextInt(0, 2);
                if (random == 0)
                    tempGene[i] = temp1Gene[i].copy();
                else
                    tempGene[i] = temp2Gene[i].copy();
            }
            //Test if position overlapping
            ArrayList<Pos> testing = new ArrayList<Pos>();
            for (Gene g : tempGene) {
                for (Pos p : g.pos) {
                    if (testing.contains(p)) {
                        //If it has overlapping, roll back to crossover
                        continue crossover;
                    } else {
                        testing.add(p);
                    }
                }
            }
            break;
        }
        //Check resource
        if (resourceLimit <= 0) return;

        //===========================================
        //MUTATION - Randomly gene-mutate
        //===========================================
        //afterCrossOver is a pointer for tempGene once met overlapping position
        Gene[] afterCrossOver = new Gene[battleShips.length];
        for (int i = 0; i < tempGene.length; i++) {
            afterCrossOver[i] = tempGene[i].copy();
        }
        //Mutation
        mutation:
        while (resourceLimit > 0) {
            tempGene = afterCrossOver;
            //For each gene to mutate
            for (int i = 0; i < tempGene.length && resourceLimit > 0; i++) {
                resourceLimit--;
                //The probability of mutation is 50%
                int doMutate = ThreadLocalRandom.current().nextInt(0, 2);
                if (doMutate == 0) {
                    //0 - No mutation, go on to next
                    continue;
                } else {
                    //1 - Does mutation
                    //Set mutation boolean to check boundary
                    boolean mutation = false;
                    //ori and forward is to determine how the ship shifting
                    //ori - false - horizon; true - vertical
                    //forward - false - backward, decrease; true - forward, increase
                    int ori = ThreadLocalRandom.current().nextInt(0, 2);
                    int forward = ThreadLocalRandom.current().nextInt(0, 2);
                    if (ori == 0) {
                        if (forward == 0) {
                            for (Pos p : tempGene[i].pos) {
                                mutation = p.col + 1 < xHits.length;
                                if (!mutation) {
                                    break;
                                }
                            }
                            if (mutation)
                                for (Pos p : tempGene[i].pos)
                                    p.col++;
                            else
                                i--;
                        } else {
                            for (Pos p : tempGene[i].pos) {
                                mutation = p.col - 1 > -1;
                                if (!mutation) {
                                    break;
                                }
                            }
                            if (mutation)
                                for (Pos p : tempGene[i].pos)
                                    p.col--;
                            else
                                i--;
                        }
                    } else {
                        if (forward == 0) {
                            for (Pos p : tempGene[i].pos) {
                                mutation = p.row + 1 < xHits.length;
                                if (!mutation) {
                                    break;
                                }
                            }
                            if (mutation)
                                for (Pos p : tempGene[i].pos)
                                    p.row++;
                            else
                                i--;
                        } else {
                            for (Pos p : tempGene[i].pos) {
                                mutation = p.row - 1 > 0;
                                if (!mutation) {
                                    break;
                                }
                            }
                            if (mutation)
                                for (Pos p : tempGene[i].pos)
                                    p.row--;
                            else
                                i--;
                        }
                    }
                }
            }
            //Test if position overlapping
            ArrayList<Pos> testing = new ArrayList<Pos>();
            for (Gene g : tempGene) {
                for (Pos p : g.pos) {
                    if (testing.contains(p)) {
                        //If it has overlapping, roll back
                        continue mutation;
                    } else {
                        testing.add(p);
                    }
                }
            }
            break;
        }
        //Check resource
        if (resourceLimit <= 0) return;

        //===========================================
        //Birth - Assign new gene to game solution
        //===========================================
        try {
            for (Gene g : tempGene) {
                for (Pos p : g.pos) {
                    //Give birth for the gene
                    temp.placeShip(p.row, p.col, g.ship);
                }
            }
        } catch (Exception e) {
            System.err.println("Could not give birth to gene: \n"  +
                    Arrays.toString(tempGene));
        }
        //Set new birth to population
        solutions[0] = temp;
        solutions[1] = generate(1)[0];

        //===========================================
        //KILL - Kill bad solution
        //===========================================
        //Get the bad score
        int badScore = solutions[solutions.length - 1].getScores();
        for (int i = solutions.length - 1; i > -1; i--) {
            if (solutions[i].getScores() == badScore)
                //Kill it and re-produce one to remain the size of population the same
                solutions[i] = generate(1)[0];
        }
    }

    /**
     * Verify the scores that the game g has
     *
     * @param g A solution of game
     */
    private static void verify(Game g) {
        int score = 0;
        //Calculate x scores
        for (int i = 0; i < g.xScoresCol.length; i++) {
            score += Math.abs(xHits[i] - g.getXScore(i));
        }
        //Calculate y scores
        for (int i = 0; i < g.yScoresRow.length; i++) {
            score += Math.abs(yHits[i] - g.getYScore(i));
        }
        //Set score to game
        g.setScores(score);
    }

    /**
     * Identically generate new games of solution
     *
     * @param amount How many solution to generate
     * @return An array of identical solutions
     */
    private static Game[] generate(int amount) {
        //Return array initialisation
        Game[] tempGames = new Game[amount];
        //For each index in array to generate
        for (int index = 0; index < amount; index++) {
            //Initialise a new game
            Game temp = new Game(xHits.length, yHits.length);
            //For each battle ship to place
            for (int i = 0; i < battleShips.length; i++) {
                //Randomly pick up a main position
                int r = ThreadLocalRandom.current().nextInt(0, xHits.length);
                int c = ThreadLocalRandom.current().nextInt(0, yHits.length);
                //Since we have four different cases - horizon with forward or backward; OR vertical with forward or backward
                int randomTestCase = ThreadLocalRandom.current().nextInt(0, 4);
                //Get the current ship to place
                int ship = battleShips[i];
                //Switch each case to test if this position can place that ship
                switch (randomTestCase) {
                    case 0:
                        //Place horizon x forward ships
                        if (!temp.isOccupied(r, c, ship, true, true)) {
                            for (int place = 0; place < ship; place++) {
                                temp.placeShip(r, c + place, ship);
                            }
                        } else {
                            i--;
                        }
                        break;
                    case 1:
                        //Place horizon x backward ships
                        if (!temp.isOccupied(r, c, ship, true, false)) {
                            for (int place = 0; place < ship; place++) {
                                temp.placeShip(r, c - place, ship);
                            }
                        } else {
                            i--;
                        }
                        break;
                    case 2:
                        //Place vertical y forward ships
                        if (!temp.isOccupied(r, c, ship, false, true)) {
                            for (int place = 0; place < ship; place++) {
                                temp.placeShip(r + place, c, ship);
                            }
                        } else {
                            i--;
                        }
                        break;
                    case 3:
                        //Place vertical y backward ships
                        if (!temp.isOccupied(r, c, ship, false, false)) {
                            for (int place = 0; place < ship; place++) {
                                temp.placeShip(r - place, c, ship);
                            }
                        } else {
                            i--;
                        }
                        break;
                }
            }
            //Set a distinct boolean
            boolean distinct = true;
            //For each games already in returning array to test
            for (Game g : tempGames) {
                if (g != null) {
                    //Call game compare method to test ships position
                    if (g.compare(temp)) {
                        distinct = false;
                    }
                } else
                    break;
            }
            //if this new game is identical or say distinct
            if (distinct)
                //Add to array
                tempGames[index] = (temp);
            else
                //Otherwise roll back
                index--;
        }
        //Return the array
        return tempGames;
    }

    /**
     * Game class is for recording a solution of puzzle
     * It implements Comparable interface to use sorting method offered by Java library
     */
    static class Game implements Comparable<Game> {
        //grid is the sea
        int[][] grid;
        //each x column scores
        int[] xScoresCol;
        //each y row scores
        int[] yScoresRow;
        //total scores
        int scores;

        /**
         * Normal Constructor
         * @param rSize row size
         * @param cSize column size
         */
        Game(int rSize, int cSize) {
            grid = new int[rSize][cSize];
            xScoresCol = new int[cSize];
            yScoresRow = new int[rSize];
        }

        /**
         * Copy Constructor
         * @param grid grid
         * @param xScoresCol xScoresCol
         * @param yScoresRow yScoresRow
         * @param scores scores
         */
        Game(int[][] grid, int[] xScoresCol, int[] yScoresRow, int scores) {
            this.grid = new int[grid.length][grid[0].length];
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    this.grid[i][j] = grid[i][j];
                }
            }
            this.xScoresCol = new int[xScoresCol.length];
            System.arraycopy(xScoresCol, 0, this.xScoresCol, 0, xScoresCol.length);

            this.yScoresRow = new int[yScoresRow.length];
            System.arraycopy(yScoresRow, 0, this.yScoresRow, 0, yScoresRow.length);

            this.scores = scores;
        }

        /**
         * Place a ship into position
         * @param r row number
         * @param c column number
         * @param type size of ship
         */
        void placeShip(int r, int c, int type) {
            grid[r][c] = type;
            xScoresCol[c]++;
            yScoresRow[r]++;
        }

        /**
         * Check if the positions are occupied by a ship already
         * @param r row number
         * @param c column number
         * @param range range of checking
         * @param ori ship orientation
         * @param forward ship forward or backward
         * @return boolean value of occupied(True) or not occupied(False)
         */
        boolean isOccupied(int r, int c, int range, boolean ori, boolean forward) {
            //ori - false - horizon; true - vertical
            //forward - false - backward, decrease; true - forward, increase
            if (ori) {
                //horizon - modify column
                if (forward) {
                    //Check the range if it is out of boundary
                    if (c + range <= xHits.length) {
                        //For each position to check
                        for (int i = 0; i < range; i++) {
                            if (grid[r][c] > 0)
                                return true;
                            c++;
                        }
                        return false;
                    } else
                        return true;
                } else {
                    if (c - range >= -1) {
                        for (int i = 0; i < range; i++) {
                            if (grid[r][c] > 0)
                                return true;
                            c--;
                        }
                        return false;
                    } else
                        return true;
                }
            } else {
                //vertical - modify row
                if (forward) {
                    if (r + range <= yHits.length) {
                        for (int i = 0; i < range; i++) {
                            if (grid[r][c] > 0)
                                return true;
                            r++;
                        }
                        return false;
                    } else
                        return true;
                } else {
                    if (r - range >= -1) {
                        for (int i = 0; i < range; i++) {
                            if (grid[r][c] > 0)
                                return true;
                            r--;
                        }
                        return false;
                    } else
                        return true;
                }
            }
        }

        /**
         * A getter of grid
         * @return 2D array of grid sea
         */
        int[][] getGrid() {
            return grid;
        }

        /**
         * A getter of x scores at index
         * @param index which x column
         * @return the score of that column
         */
        int getXScore(int index) {
            return xScoresCol[index];
        }

        /**
         * A getter of y scores at index
         * @param index which y row
         * @return the score of that row
         */
        int getYScore(int index) {
            return yScoresRow[index];
        }

        /**
         * For this game setting a scores
         * @param scores score of this game
         */
        void setScores(int scores) {
            this.scores = scores;
        }

        /**
         * Get scores
         * @return scores of this game
         */
        int getScores() {
            return scores;
        }

        /**
         * Compare a game if it is the same
         * @param g other game
         * @return Same(True) or Identical(False)
         */
        boolean compare(Game g) {
            //If the length of grid is not the same
            if (this.grid.length != g.grid.length || this.grid[0].length != g.grid[0].length)
                //return false
                return false;
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    //Once there is a grid is not the same
                    if (grid[i][j] != g.grid[i][j])
                        //return false
                        return false;
                }
            }
            return true;
        }

        /**
         * Override method from Comparable interface
         * @param o other game
         * @return int of comparision
         */
        @Override
        public int compareTo(Game o) {
            return this.getScores() - o.getScores();
        }

        /**
         * Override method
         * @return String statement of this game
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    sb.append(grid[i][j]);
                    sb.append(" ");
                }
                sb.append("\n");
            }

            return "Grid{" +
                    "grid=" + sb.toString() +
                    ", xScores=" + Arrays.toString(xScoresCol) +
                    ", yScores=" + Arrays.toString(yScoresRow) +
                    ", scores=" + scores +
                    '}';
        }

        /**
         * copy method to copy a same game
         * @return A copied game
         */
        Game copy() {
            return new Game(this.grid, this.xScoresCol, this.yScoresRow, this.scores);
        }
    }

    /**
     * Gene class stored solution's gene
     */
    static class Gene {
        //ship type
        int ship;
        //positions of that ship
        ArrayList<Pos> pos;

        /**
         * Normal constructor
         * @param ship ship type
         */
        Gene(int ship) {
            this.ship = ship;
            pos = new ArrayList<Pos>();
        }

        /**
         * For copying method, generate a same valued gene
         * @param ship
         * @param pos
         */
        Gene(int ship, ArrayList<Pos> pos) {
            this.ship = ship;
            this.pos = new ArrayList<Pos>();
            for (Pos p : pos) {
                this.pos.add(p.copy());
            }
        }

        /**
         * Add position to list
         * @param r row number
         * @param c column number
         */
        void addPos(int r, int c) {
            pos.add(new Pos(r, c));
        }

        /**
         * copy method
         * @return A copied gene
         */
        Gene copy() {
            return new Gene(this.ship, this.pos);
        }
    }

    /**
     * Position class is to store row number and column number
     */
    static class Pos {
        int row;
        int col;

        /**
         * Normal constructor
         * @param r row number
         * @param c column number
         */
        Pos(int r, int c) {
            this.row = r;
            this.col = c;
        }

        /**
         * copy method
         * @return A copied position
         */
        Pos copy() {
            return new Pos(row, col);
        }

        /**
         * Override equals method to compare position
         * @param o
         * @return
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pos pos = (Pos) o;
            return row == pos.row &&
                    col == pos.col;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
}
