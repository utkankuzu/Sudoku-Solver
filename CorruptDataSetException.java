/**
 * Created by Utkan Kuzu on 4/19/19.
 */

public class CorruptDataSetException extends Exception{
    public CorruptDataSetException(){
        super("The data set has been corrupted.");
    }
}