package chat.teco.tecochat.comment.execption;

import chat.teco.tecochat.common.exception.BaseException;
import chat.teco.tecochat.common.exception.BaseExceptionType;

public class CommentException extends BaseException {

    private final CommentExceptionType exceptionType;

    public CommentException(CommentExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public CommentException(CommentExceptionType exceptionType, Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
