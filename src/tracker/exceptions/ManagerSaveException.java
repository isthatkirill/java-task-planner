package tracker.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(final String message, Throwable cause) {
        super(message, cause);
    }
}
