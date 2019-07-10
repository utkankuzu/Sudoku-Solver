/**
 * Created by Utkan Kuzu on 4/19/19.
 */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HelperClass {

    public static Sudoku createPuzzle() throws IOException, java.lang.Exception, CorruptDataSetException{
        String[][] myWholeMatrix = new String[9][9];
        createInitialSudokuMatrix(myWholeMatrix);
        try {
            //verify the initial dataset read from file is not corrupt
            System.out.println("Initial verification started.");
            verifyWholeMatrix(myWholeMatrix);
        }
        catch (CorruptDataSetException e) {
            System.out.println("Dataset in the source file is corrupt, try again.");
            System.out.println(e);
            createPuzzle(); //recursively try to create a new puzzle if the end-user is willing to fix the file
        }

        return new Sudoku(myWholeMatrix); //instantiate the root sudoku puzzle
    }

    private static void createInitialSudokuMatrix(String[][] wholeMatrix) throws java.io.FileNotFoundException, java.io.IOException, java.lang.Exception {
        try {
            System.out.println("------------------------------------------------------------");
            System.out.println("Name your file myfile.txt and move into: " + System.getProperty("user.dir"));
            System.out.println("------------------------------------------------------------");

            System.out.println("Press Enter to start.");
            // Waiting user to press Enter on Console
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in), 1);

            //using BufferedReader to stream file IO
            //making the code portable for any OS type
            BufferedReader reader = new BufferedReader(new FileReader(System.getProperty("user.dir") + File.separatorChar + "myfile.txt"));
            stdin.readLine();

            //read 8 lines per reader and 8 cells per line to create the initial sudoku matrix
            for (int i=0; i<=8; i++){
                String line = reader.readLine();
                if (line!=null) {
                    for (int e=0; e<=8; e++){
                        //verify each cell before inserting to the matrix
                        wholeMatrix[i][e] = verifyCell(Character.toString(line.charAt(e)));
                    }
                }
            }
            reader.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException has occurred, try again.");
            System.out.println(e);
            createInitialSudokuMatrix(wholeMatrix);
        }
        catch (IOException e) {
            System.out.println("IO Exception has occurred, try again.");
            System.out.println(e);
            createInitialSudokuMatrix(wholeMatrix);
        }
        catch (java.lang.Exception e) {
            System.out.println("File format exception has occurred try again.");
            System.out.println(e);
            createInitialSudokuMatrix(wholeMatrix);
        }
    }

    public static void verifyWholeMatrix (String[][] wholeMatrix) throws CorruptDataSetException{
        verifyHorizontallyAndVertically(wholeMatrix);
        verifyCubically(wholeMatrix);
    }

    public static void verifyHorizontallyAndVertically(String[][] wholeMatrix) throws CorruptDataSetException{
        for (int x = 0; x <= 8; x++) {
            SudokuVector horizontalVector = new SudokuVector();
            SudokuVector verticalVector = new SudokuVector();
            for (int y = 0; y <= 8; y++) {
                horizontalVector.verifyDuplicateCell(wholeMatrix[x][y]);
                verticalVector.verifyDuplicateCell(wholeMatrix[y][x]);
            }
        }
    }

    public static void verifyCubically(String[][] wholeMatrix) throws CorruptDataSetException{
        for (int x=1; x<=3; x++){
            for (int y=1; y<=3; y++){
                SudokuVector cubicalVector = new SudokuVector();
                for (int z=1; z<=3; z++){
                    for (int i=1; i<=3; i++){
                        cubicalVector.verifyDuplicateCell(wholeMatrix[((x*3)-(3-z))-1][((y*3)-(3-i))-1]);
                    }
                }
            }
        }
    }


    public static String verifyCell(String cell) {
        //verify each cell is a single digit countable decimal or assign "*" as blank cell indicator
        ArrayList<String> completeNineVerifier = factoryCompleteNine();
        if (completeNineVerifier.indexOf(cell) < 0) {
            return "*";
        } else {
            return cell;
        }
    }


    public static ArrayList<String> factoryCompleteNine(){
        ArrayList<String> completeNine = new ArrayList<String>();
        completeNine.add("1");
        completeNine.add("2");
        completeNine.add("3");
        completeNine.add("4");
        completeNine.add("5");
        completeNine.add("6");
        completeNine.add("7");
        completeNine.add("8");
        completeNine.add("9");

        //return complete array of single digit countable decimals
        return completeNine;
    }

    public static HashMap<String,Integer> factoryCompleteEliminateNine() {
        HashMap<String,Integer> completeEliminateNine = new HashMap<String,Integer>();
        completeEliminateNine.put("1",0);
        completeEliminateNine.put("2",0);
        completeEliminateNine.put("3",0);
        completeEliminateNine.put("4",0);
        completeEliminateNine.put("5",0);
        completeEliminateNine.put("6",0);
        completeEliminateNine.put("7",0);
        completeEliminateNine.put("8",0);
        completeEliminateNine.put("9",0);

        //return complete map with a key set of single digit countable decimals and integers to be used in counter loops
        return completeEliminateNine;
    }
}