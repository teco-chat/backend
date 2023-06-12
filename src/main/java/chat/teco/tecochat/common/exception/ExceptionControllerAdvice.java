package chat.teco.tecochat.common.exception;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    private static final String BAD_REQUEST_ERROR_CODE = "1000";
    private static final String UNEXPECTED_ERROR_CODE = "9999";

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 400
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ExceptionResponse> handleException(MethodArgumentNotValidException e) {
        String errorMessage = e.getFieldErrors().stream()
                .map(it -> it.getField() + " : " + it.getDefaultMessage())
                .collect(Collectors.joining("\n"));
        log.error("요청 필드의 형식이 올바르지 않습니다. [%s]".formatted(errorMessage));
        ExceptionResponse exceptionResponse = new ExceptionResponse(BAD_REQUEST_ERROR_CODE,
                "요청 필드의 형식이 올바르지 않습니다. [%s]".formatted(errorMessage));
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    /**
     * 400
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ExceptionResponse> handleException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return ResponseEntity.badRequest().body(
                new ExceptionResponse(BAD_REQUEST_ERROR_CODE, "Json 매핑 시 오류 발생")
        );
    }

    @ExceptionHandler(BaseException.class)
    ResponseEntity<ExceptionResponse> handleException(BaseException e) {
        BaseExceptionType type = e.exceptionType();
        log.error("[ERROR] MESSAGE: {}, CAUSE: {}", type.errorMessage(), e.getCause());
        return new ResponseEntity<>(
                new ExceptionResponse(String.valueOf(type.errorCode()), type.errorMessage()),
                type.httpStatus());
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ExceptionResponse> handleException(Exception e) {
        log.error("[ERROR] 알 수 없는 예외가 발생했습니다", e);
        return ResponseEntity.internalServerError().body(
                new ExceptionResponse(UNEXPECTED_ERROR_CODE, "서버 내부에서 알 수 없는 오류가 발생했습니다.")
        );
    }
}
