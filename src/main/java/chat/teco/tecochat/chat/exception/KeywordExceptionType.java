package chat.teco.tecochat.chat.exception;

import chat.teco.tecochat.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum KeywordExceptionType implements BaseExceptionType {

    CAN_NOT_EXTRACTED_KEYWORD(
            500,
            null,
            "키워드 추출에 실패했습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    KeywordExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public int errorCode() {
        return errorCode;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }

    @Override
    public String errorMessage() {
        return errorMessage;
    }
}
