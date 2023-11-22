package chat.teco.tecochat.ui

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors

private const val BAD_REQUEST_ERROR_CODE = "1000"
private const val UNEXPECTED_ERROR_CODE = "9999"

@RestControllerAdvice
class ExceptionHandler {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleException(exception: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse> {
        val errorMessage = exception.fieldErrors.stream()
            .map { it: FieldError -> it.field + " : " + it.defaultMessage }
            .collect(Collectors.joining("\n"))
        log.error("요청 필드의 형식이 올바르지 않습니다. $errorMessage")
        val exceptionResponse = ExceptionResponse(
            BAD_REQUEST_ERROR_CODE,
            "요청 필드의 형식이 올바르지 않습니다. $errorMessage"
        )
        return ResponseEntity.badRequest()
            .body(exceptionResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleException(exception: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse> {
        log.error(exception.message)
        return ResponseEntity.badRequest().body(
            ExceptionResponse(BAD_REQUEST_ERROR_CODE, "Json 매핑 시 오류 발생")
        )
    }

    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleBadRequestException(exception: RuntimeException): ResponseEntity<ExceptionResponse> {
        log.error("[ERROR] 알 수 없는 예외가 발생했습니다", exception)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ExceptionResponse(BAD_REQUEST_ERROR_CODE, exception.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<ExceptionResponse> {
        log.error("[ERROR] 알 수 없는 예외가 발생했습니다", exception)
        return ResponseEntity.internalServerError()
            .body(ExceptionResponse(UNEXPECTED_ERROR_CODE, "서버 내부에서 알 수 없는 오류가 발생했습니다."))
    }
}

data class ExceptionResponse(val code: String, val message: String?)
