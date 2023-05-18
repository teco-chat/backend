package chat.teco.tecochat.acceptance.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.common.exception.ExceptionResponse;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpStatus;

@SuppressWarnings("NonAsciiCharacters")
public class AcceptanceTestSteps {

    public static HttpStatus 정상_요청 = HttpStatus.OK;
    public static HttpStatus 정상_생성 = HttpStatus.CREATED;
    public static HttpStatus 비정상_요청 = HttpStatus.BAD_REQUEST;
    public static HttpStatus 권한_없음 = HttpStatus.FORBIDDEN;
    public static HttpStatus 찾을수_없음 = HttpStatus.NOT_FOUND;

    public static void 요청_결과의_상태를_검증한다(ExtractableResponse<Response> 요청_결과, HttpStatus 상태) {
        assertThat(요청_결과.statusCode()).isEqualTo(상태.value());
    }

    public static void 발생한_예외를_검증한다(
            ExtractableResponse<Response> 응답,
            BaseExceptionType 예외_타입
    ) {
        ExceptionResponse exceptionResponse = 응답.as(ExceptionResponse.class);
        assertThat(exceptionResponse.code())
                .isEqualTo(String.valueOf(예외_타입.errorCode()));
        assertThat(exceptionResponse.message())
                .isEqualTo(예외_타입.errorMessage());
    }

    public static RequestSpecification given() {
        return RestAssured
                .given().log().all()
                .contentType(APPLICATION_JSON_VALUE);
    }

    public static RequestSpecification given(String 크루명) {
        return RestAssured
                .given().log().all()
                .header(new Header("name", encode(크루명)))
                .contentType(APPLICATION_JSON_VALUE);
    }

    private static String encode(String name) {
        return new String(Base64.getEncoder().encode(name.getBytes()));
    }

    public static <T> List<T> 비어있음() {
        return Collections.emptyList();
    }
}
