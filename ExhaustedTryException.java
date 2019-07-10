/**
 * Created by Utkan Kuzu on 4/19/19.
 */

public class ExhaustedTryException extends Exception{
    public ExhaustedTryException(){
        super("Retries have been exhausted.");
    }
}
