package cloud.simpledoc.domain.model.exception;

public class AlreadyExistingException extends Exception {
    public AlreadyExistingException(String message) {
        super(message);
    }
}
