package chat.teco.tecochat.common.exception;

public abstract class BaseException extends RuntimeException {

    public BaseException() {
    }

    public BaseException(final String message) {
        super(message);
    }

    public BaseException(final Throwable cause) {
        super(cause);
    }

    public BaseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public abstract BaseExceptionType exceptionType();
}
