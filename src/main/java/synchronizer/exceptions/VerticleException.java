package synchronizer.exceptions;

class VerticleException extends Exception {
    public VerticleException() {
        super();
    }

    public VerticleException(String message) {
        super(message);
    }

    public VerticleException(Throwable cause) {
        super(cause);
    }

    public VerticleException(String message, Throwable cause) {
        super(message, cause);
    }
}
