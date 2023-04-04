package exceptions;

public class EqualTimeException extends RuntimeException {
    public EqualTimeException() {
        super();
    }

    public EqualTimeException(String message) {
        super(message);
    }

    public EqualTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EqualTimeException(Throwable cause) {
        super(cause);
    }
}
