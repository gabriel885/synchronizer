package synchronizer.exceptions;

class ServiceIOException extends ServiceException {
    public ServiceIOException() {
        super();
    }

    public ServiceIOException(String message) {
        super(message);
    }

    public ServiceIOException(Throwable cause) {
        super(cause);
    }

    public ServiceIOException(String message, Throwable cause) {
        super(message, cause);
    }
}
