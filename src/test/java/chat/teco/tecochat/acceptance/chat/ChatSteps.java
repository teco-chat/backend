package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.application.CopyChatResponse;
import chat.teco.tecochat.application.UpdateChatTitleRequest;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.LikeCond;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.support.ui.PageResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("NonAsciiCharacters")
public class ChatSteps {

    public static ExtractableResponse<Response> 채팅_제목_수정_요청(
            Long 채팅_ID,
            String 이름,
            String 변경할_제목
    ) {
        return given(이름)
                .body(new UpdateChatTitleRequest(변경할_제목))
                .when()
                .patch("/chats/{id}", 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 채팅_복제_요청(String 이름, Long 채팅_ID) {
        return given(이름)
                .when()
                .post("/chats/copy/{id}", 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static Long 복제된_채팅_ID_반환(ExtractableResponse<Response> 응답) {
        CopyChatResponse response = 응답.as(CopyChatResponse.class);
        return response.getCopiedChatId();
    }

    public static ExtractableResponse<Response> 단일_채팅_조회_요청(Long 채팅_ID, String 이름) {
        return given(이름)
                .when()
                .get("/chats/{id}", 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static void 단일_채팅_조회_결과를_확인한다(
            ExtractableResponse<Response> 응답,
            QueryChatByIdResponse 예상_결과
    ) {
        QueryChatByIdResponse chatQueryResponse = 응답.as(QueryChatByIdResponse.class);
        assertThat(chatQueryResponse)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }

    public static ExtractableResponse<Response> 이름_과정_제목_좋아요_기간으로_검색_요청(
            Map<String, Object> 요청_파라미터들
    ) {
        return given()
                .when()
                .queryParams(요청_파라미터들)

                .get("/chats")
                .then().log().all()
                .extract();
    }

    public static Map<String, Object> 요청_파라미터들() {
        return new HashMap<>();
    }

    public static void 이름_조건(
            Map<String, Object> 요청_파라미터들,
            String 이름
    ) {
        요청_파라미터들.put("name", 이름);
    }

    public static void 과정_조건(
            Map<String, Object> 요청_파라미터들,
            Course 과정
    ) {
        요청_파라미터들.put("course", 과정);
    }

    public static void 제목_조건(
            Map<String, Object> 요청_파라미터들,
            String 제목
    ) {
        요청_파라미터들.put("title", 제목);
    }

    public static void 좋아요_기간_조겅(
            Map<String, Object> 요청_파라미터들,
            LikeCond 좋아요_기간
    ) {
        요청_파라미터들.put("likeCond", 좋아요_기간);
    }

    public static void 검색_결과의_내용_검증(
            ExtractableResponse<Response> 응답,
            List<SearchChatResponse> 예상_결과
    ) {
        PageResponse<SearchChatResponse> 페이지_결과 = 응답.as(
                new TypeRef<>() {
                });
        List<SearchChatResponse> content = 페이지_결과.getContent();
        assertThat(content)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }

    public static void 검색_결과_없음(
            ExtractableResponse<Response> 응답
    ) {
        PageResponse<SearchChatResponse> 페이지_결과 = 응답.as(
                new TypeRef<>() {
                });
        assertThat(페이지_결과.getTotalElements()).isEqualTo(0);
    }
}
