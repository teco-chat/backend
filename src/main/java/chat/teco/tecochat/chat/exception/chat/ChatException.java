package chat.teco.tecochat.chat.exception.chat;

import chat.teco.tecochat.common.exception.BaseException;
import chat.teco.tecochat.common.exception.BaseExceptionType;

public class ChatException extends BaseException {

    private final ChatExceptionType exceptionType;

    public ChatException(final ChatExceptionType exceptionType) {
        super(exceptionType.errorMessage());
        this.exceptionType = exceptionType;
    }

    public ChatException(final ChatExceptionType exceptionType, final Throwable cause) {
        super(exceptionType.errorMessage(), cause);
        this.exceptionType = exceptionType;
    }

    @Override
    public BaseExceptionType exceptionType() {
        return exceptionType;
    }
}
