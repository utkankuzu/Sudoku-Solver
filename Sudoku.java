/**
 * Created by Utkan Kuzu on 4/19/19.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Sudoku {
    private String[][] myWholeMatrix;
    private HashMap<Integer, ArrayList<String>> myCubicalMap = new HashMap<Integer,ArrayList<String>>();
    private ArrayList<String> possibleValues;
    private minPossibleValueCoordinates myMinPossibleValueCoordinates;
    private boolean puzzleComplete;
    //private Sudoku myChild;
    private Sudoku myParent;

    //constructor method to instantiate the root sudoku puzzle
    public Sudoku(String[][] wholeMatrix){
        myWholeMatrix = wholeMatrix;
        placeCellsCubical();
        myMinPossibleValueCoordinates = new minPossibleValueCoordinates();
        puzzleComplete = false;
        myParent = null;
        System.out.println("New root puzzle created.");
        printWholeMatrix();
    }

    //constructor method to instantiate the child sudoku puzzle
    public Sudoku(String[][] wholeMatrix, Sudoku parent){
        myWholeMatrix = wholeMatrix;
        placeCellsCubical();
        myMinPossibleValueCoordinates = new minPossibleValueCoordinates();
        puzzleComplete = false;
        myParent = parent;
        System.out.println("New child puzzle created.");
        printWholeMatrix();
    }

    //starts the complete loop of all cells until the puzzle is solved
    public void solveMe() throws CorruptDataSetException {
        boolean loop = true;
        while (loop) {
            loop = identifyCellToSolve();
        }
        if (!puzzleComplete) {
            System.out.println("The puzzle is not completed!");
            this.printWholeMatrix();
            tryPossibleValue();

        } else {
            System.out.println("The puzzle is completed!");
            this.printWholeMatrix();
            try {
                System.out.println("Final verification started.");
                HelperClass.verifyWholeMatrix(myWholeMatrix);
            }
            catch (CorruptDataSetException e) {
                System.out.println("The final result is not correct, terminating the thread.");
                System.out.println(e);
                System.exit(0);
            }
            System.out.println("Final verification successful.");
        }
    }

    //tries a possible value if both linear and eliminator loops fail to identify a correct value of a blank cell
    //create a child sudoku puzzle by passing a new matrix including the cell with trial value
    private void tryPossibleValue(){
        try {
            Sudoku myChild = new Sudoku(myMinPossibleValueCoordinates.assignValueToTry(myWholeMatrix), this);
            myChild.solveMe();
        }
        catch (CorruptDataSetException theException) {
            System.out.println(theException);
            //if try fails try again using the next possible value for the same blank cell
            tryPossibleValue();
        }
        catch (ExhaustedTryException theException) {
            System.out.println(theException);
            //if all trials fail go back to parent level to try another cell/value
            if (myParent!=null){
                myParent.tryPossibleValue();
            }
        }
    }

    //loop 81 cells identify blank cells and apply linear and eliminator loops for each blank cell
    private boolean identifyCellToSolve() throws CorruptDataSetException{
        puzzleComplete = true;
        for (int x=1; x<=3; x++){
            for (int y=1; y<=3; y++){
                for (int z=1; z<=3; z++){
                    for (int i=1; i<=3; i++){
                        if (myWholeMatrix[((x*3)-(3-z))-1][((y*3)-(3-i))-1].equalsIgnoreCase("*")) {
                            puzzleComplete = false;
                            if (solveLinear((((x*3)-(3-z))-1), (((y*3)-(3-i))-1), (((x*3)-(3-y))-1))) {
                                return true;
                            }
                            if (solveEliminator(x,y,z,i)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    //linear loop applies the most basic sudoku logic to identify the value of a blank cell
    private boolean solveLinear(Integer x, Integer y, Integer z) throws CorruptDataSetException {
        ArrayList<String> completeNine = HelperClass.factoryCompleteNine();
        for (int i=0; i<=8; i++) {
            //remove the horizontal values
            completeNine.remove(myWholeMatrix[x][i]);
            //remove the vertical values
            completeNine.remove(myWholeMatrix[i][y]);
            //remove the cubical values
            completeNine.remove(myCubicalMap.get(z).get(i));
        }

        //if there is only one element left in the completeNine it is the value of the blank cell
        if (completeNine.size()==1) {
            myWholeMatrix[x][y] = completeNine.get(0);
            placeCellsCubical();
            return true;
        }

        //if all elements are removed from completeNine there is no possible value for the blank cell
        //which means the trial value assigned was not correct
        if (completeNine.size()==0) {
            System.out.println("x: " + x );
            System.out.println("y: " + y );
            throw new CorruptDataSetException();
        }

        //if the code reaches this point there are multiple possible values for the blank cell
        possibleValues = completeNine;
        //save the blank cell coordinates and possible values for new value trial logic
        //saves for the cell with minimum possible value set
        myMinPossibleValueCoordinates.setMinPossibleValueCoordinates(possibleValues,x,y);

        return false;
    }

    //eliminator loop applies second most basic sudoku logic to identify the value of a blank cell
    private boolean solveEliminator(Integer x, Integer y, Integer z, Integer i) {
        HashMap<String,Integer> myCounter = HelperClass.factoryCompleteEliminateNine();
        //loop the cubical blank neighbor cells of the target blank cell
        for (int n=1; n<=3; n++) {
            for (int m=1; m<=3; m++){
                //skip if it is the blank cell itself
                if (!(n==z && m==i)){
                    //apply to the cubical neighbour blank cells only
                    if (myWholeMatrix[((x*3)-(3-n))-1][((y*3)-(3-m))-1].equalsIgnoreCase("*")) {
                        //count the existing values with in the same horizontal and vertical lines of the blank neighbor cell
                        eliminateCounter(myCounter,((x*3)-(3-n))-1, ((y*3)-(3-m))-1);
                    }
                }
            }
        }

        //identify the empty cell count of the cube
        int myEmptyCellCount = cubicalEmptyCellCounter(((x*3)-(3-y))-1);

        //if the counter map has a key with a value greater than empty cell count - 1 it is the value of the target blank cell
        for (HashMap.Entry<String, Integer> entry : myCounter.entrySet()) {
            Integer value = entry.getValue();
            if (value > 0){
                if (value >= myEmptyCellCount-1){
                    myWholeMatrix[((x*3)-(3-z))-1][((y*3)-(3-i))-1] = entry.getKey();
                    placeCellsCubical();
                    return true;
                }
            }
        }
        return false;
    }

    //counts the existing values horizontally and vertically then assigns the value to the counter map if it was contained as a possible value
    private void eliminateCounter(HashMap<String,Integer> counter, int axisX, int axisY) {
        ArrayList<String> existingValues = new ArrayList<String>();
        for (int i=0; i<=8; i++) {
            //this logic ensures that a value is added only once
            if (possibleValues.indexOf(myWholeMatrix[axisX][i]) >= 0) {
                if (existingValues.indexOf(myWholeMatrix[axisX][i])<0) {
                    existingValues.add(myWholeMatrix[axisX][i]);
                }
            }
            if (possibleValues.indexOf(myWholeMatrix[i][axisY]) >= 0) {
                if (existingValues.indexOf(myWholeMatrix[i][axisY])<0) {
                    existingValues.add(myWholeMatrix[i][axisY]);
                }
            }
        }

        //this for loop ensures that a value is counted only once
        for (String s:existingValues) {
            counter.put(s, (counter.get(s) + 1));
        }
    }

    private int cubicalEmptyCellCounter(int theCubeNo){
        int countEmptyCells = 0;
        for (int i=0; i<=8; i++){
            if (myCubicalMap.get(theCubeNo).get(i).equalsIgnoreCase("*")){
                countEmptyCells++;
            }
        }
        return countEmptyCells;
    }

    //uses the whole sudoku matrix to create 9*(3*3) cubical matrix map
    private void placeCellsCubical() {
        for (int x=1; x<=3; x++){
            for (int y=1; y<=3; y++){
                ArrayList<String> newList = new ArrayList<String>();
                for (int z=1; z<=3; z++){
                    for (int i=1; i<=3; i++){
                        newList.add(myWholeMatrix[((x*3)-(3-z))-1][((y*3)-(3-i))-1]);
                    }
                }
                myCubicalMap.put(((x*3)-(3-y))-1, newList);
            }
        }
    }

    //using my own method to clone primitive 2dimension array
    private String[][] clonePrimitiveMatrix (String[][] thePrimitiveMatrix) {
        String[][] theCloneMatrix = new String[9][9];
        for (int x=0;x<=8;x++) {
            for (int y=0;y<=8;y++) {
                theCloneMatrix[x][y] = thePrimitiveMatrix[x][y];
            }
        }
        return theCloneMatrix;
    }

    ////////////////////////////
    //Wrapper class
    ///////////////////////////
    //wraps the attributes to save the coordinates and possible values of a cell
    //saves only the cell with min number of possible values
    //saves the element that has been tried and provides the next value if the trial did not succeed
    private class minPossibleValueCoordinates {
        private int mySize;
        private ArrayList<String> myPossibleValues;
        private int xAxis;
        private int yAxis;
        private int indexOfTheValueToTry;
        private String[][] childMatrix;

        protected minPossibleValueCoordinates(){
            mySize = 10;
            xAxis = 0;
            yAxis = 0;
            indexOfTheValueToTry = -1;
            childMatrix = new String[9][9];
        }

        protected void setMinPossibleValueCoordinates(ArrayList<String> possibleValues, int xAxis, int yAxis){
            if (possibleValues.size() < mySize) {
                mySize = possibleValues.size();
                this.myPossibleValues = possibleValues;
                this.xAxis = xAxis;
                this.yAxis = yAxis;
            }
        }


        //throws exhaustedTryException to ensure that the child sudoku is not the correct path
        //the logic implemented in method tryPossibleValue relies on this exception to try another cell/value in the parent sudoku object
        protected String[][] assignValueToTry(String[][] parentMatrix) throws ExhaustedTryException {
            if (indexOfTheValueToTry+1 <= mySize-1) {
                childMatrix = clonePrimitiveMatrix(parentMatrix);
                indexOfTheValueToTry++;
                childMatrix[xAxis][yAxis] = myPossibleValues.get(indexOfTheValueToTry);
                System.out.println("coordinates: " + (xAxis+1) + "*" + (yAxis+1) );
                System.out.println("possible values: " + myPossibleValues);
                System.out.println("trying: " + myPossibleValues.get(indexOfTheValueToTry));
            } else {
                throw new ExhaustedTryException();
            }
            return childMatrix;
        }
    }

    ////////////////////////////
    //Printer methods
    ///////////////////////////

    public void printWholeMatrix() {
        for (int i=0; i<=8; i++){
            for (int e=0; e<=8; e++){
                System.out.print(myWholeMatrix[i][e]);
            }
            System.out.println();
        }
        System.out.println("-------------------------");
    }

    public void printCubical() {
        for (int i=0; i<=8; i++){
            System.out.println(myCubicalMap.get(i));
        }
    }
}