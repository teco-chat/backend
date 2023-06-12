package chat.teco.tecochat.comment.execption;

import chat.teco.tecochat.common.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum CommentExceptionType implements BaseExceptionType {

    NOT_FOUND_COMMENT(
            300,
            HttpStatus.NOT_FOUND,
            "해당 댓글이 존재하지 않습니다."
    ),
    NO_AUTHORITY_UPDATE_COMMENT(
            301,
            HttpStatus.FORBIDDEN,
            "댓글을 수정할 권한이 없습니다."
    ),
    NO_AUTHORITY_DELETE_COMMENT(
            302,
            HttpStatus.FORBIDDEN,
            "댓글을 삭제할 권한이 없습니다."
    ),
    ;

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;

    CommentExceptionType(int errorCode, HttpStatus httpStatus, String errorMessage) {
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
