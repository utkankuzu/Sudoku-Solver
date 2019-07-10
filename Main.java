/**
 * Created by Utkan Kuzu on 4/19/19.
 */

public class Main {

    public static void main(String[] args) throws java.lang.Exception, java.io.IOException, CorruptDataSetException  {
        System.out.println("Welcome to Sudoku!");
        Sudoku sudoku = HelperClass.createPuzzle();
        sudoku.solveMe();
    }
}
