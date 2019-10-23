package synchronizer.exceptions;

public class ApplicationFailure extends Exception{
    public ApplicationFailure(){
        super();
    }
    public ApplicationFailure(String message){
        super(message);
    }
    public ApplicationFailure(Throwable cause) {
        super(cause);
        cause.printStackTrace();
    }
    public ApplicationFailure(String message, Throwable cause){
        super(message, cause);
    }
}
