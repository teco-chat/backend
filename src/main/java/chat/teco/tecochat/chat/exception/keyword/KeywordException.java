package chat.teco.tecochat.chat.exception.keyword;

import chat.teco.tecochat.common.exception.BaseException;
import chat.teco.tecochat.common.exception.BaseExceptionType;

public class KeywordException extends BaseException {

    private final KeywordExceptionType exceptionType;

    public KeywordException(KeywordExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public KeywordException(KeywordExceptionType exceptionType, Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
