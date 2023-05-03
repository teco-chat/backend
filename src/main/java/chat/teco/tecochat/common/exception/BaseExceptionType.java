package chat.teco.tecochat.common.exception;

import org.springframework.http.HttpStatus;

public interface BaseExceptionType {

    int errorCode();

    HttpStatus httpStatus();

    String errorMessage();
}
