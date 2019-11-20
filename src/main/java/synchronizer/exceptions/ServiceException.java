package synchronizer.exceptions;

class ServiceException extends Exception {
    ServiceException() {
        super();
    }

    ServiceException(String message) {
        super(message);
    }

    ServiceException(Throwable cause) {
        super(cause);
    }

    ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
