/**
 * Created by Utkan Kuzu on 4/19/19.
 */

import java.util.ArrayList;

public class SudokuVector {
    private ArrayList<String> myVector = new ArrayList<String>();

    public void verifyDuplicateCell(String cell) throws CorruptDataSetException {
        //verifies whether the array has duplicate cells or not except blank cell indicator "*"
        if (cell!="*") {
            if (myVector.indexOf(cell) >= 0)  {
                throw new CorruptDataSetException();
            } else {
                myVector.add(cell);
            }
        }
    }
}