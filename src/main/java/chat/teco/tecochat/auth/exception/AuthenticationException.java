package chat.teco.tecochat.auth.exception;

import chat.teco.tecochat.common.exception.BaseException;
import chat.teco.tecochat.common.exception.BaseExceptionType;

public class AuthenticationException extends BaseException {

    private final AuthenticationExceptionType exceptionType;

    public AuthenticationException(AuthenticationExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public AuthenticationException(AuthenticationExceptionType exceptionType, Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
