package chat.teco.tecochat.chat.exception.chat;

import chat.teco.tecochat.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ChatExceptionType implements BaseExceptionType {

    NOT_FOUND_CHAT(
            200,
            HttpStatus.NOT_FOUND,
            "채팅이 존재하지 않습니다."
    ),
    QUESTION_SIZE_TOO_BIG(
            201,
            HttpStatus.BAD_REQUEST,
            "질문하신 내용의 길이가 너무 깁니다. 질문의 길이를 줄여주세요"
    ),
    GPT_API_ERROR(
            203,
            HttpStatus.SERVICE_UNAVAILABLE,
            "GPT API 에 문제가 있습니다"
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    ChatExceptionType(final int errorCode, final HttpStatus httpStatus, final String errorMessage) {
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
