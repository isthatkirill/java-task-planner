package tracker.customExceptions;

public class ManagerSaveException extends Error {

    public ManagerSaveException() {
    }

    public ManagerSaveException(final String message) {
        super(message);
    }
}
