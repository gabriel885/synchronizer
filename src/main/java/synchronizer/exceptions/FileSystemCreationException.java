package synchronizer.exceptions;

public class FileSystemCreationException extends Exception {
    public FileSystemCreationException() {
        super();
    }
    public FileSystemCreationException(String message) {
        super(message);
    }
    public FileSystemCreationException(Throwable cause) {
        super(cause);
    }
    public FileSystemCreationException(String message, Throwable cause){
        super(message, cause);
    }
}
