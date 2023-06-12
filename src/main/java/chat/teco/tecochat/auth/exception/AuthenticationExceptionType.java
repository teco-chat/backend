package chat.teco.tecochat.auth.exception;

import chat.teco.tecochat.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum AuthenticationExceptionType implements BaseExceptionType {

    NO_NAME_HEADER(
            400,
            HttpStatus.UNAUTHORIZED,
            "name 헤더가 없어 인증에 실패하였습니다."
    ),
    NO_REGISTERED_MEMBER(
            401,
            HttpStatus.UNAUTHORIZED,
            "헤당 이름을 가진 회원이 없어 인증에 실패하였습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    AuthenticationExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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

