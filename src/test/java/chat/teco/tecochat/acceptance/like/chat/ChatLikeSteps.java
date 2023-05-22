package chat.teco.tecochat.acceptance.like.chat;

import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.given;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.common.presentation.PageResponse;
import chat.teco.tecochat.like.chatlike.presentation.request.PushChatLikeRequest;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikedByMemberIdResponse;
import io.restassured.common.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class ChatLikeSteps {

    public static ExtractableResponse<Response> 좋아요_요청(String 이름, Long 채팅_ID) {
        return given(이름)
                .body(new PushChatLikeRequest(채팅_ID))
                .when()
                .post("/chat-likes")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 채팅에_달린_좋아요_조회_요청(Long 채팅_ID) {
        return given()
                .when()
                .get("/chat-likes?chatId=" + 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static void 좋아요_조회_결과_검증(
            ExtractableResponse<Response> 응답,
            List<QueryChatLikeByChatIdResponse> 예상_결과
    ) {
        List<QueryChatLikeByChatIdResponse> 실제_결과 = 응답.as(new TypeRef<>() {
        });
        assertThat(실제_결과).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }

    public static ExtractableResponse<Response> 회원이_좋아요_누른_채팅_조회_요청(String 이름) {
        return given(이름)
                .when()
                .get("/chat-likes")
                .then()
                .log().all()
                .extract();
    }

    public static void 회원이_좋아요_누른_채팅_조회_결과_검증(
            ExtractableResponse<Response> 응답,
            List<QueryChatLikedByMemberIdResponse> 예상_결과
    ) {
        PageResponse<QueryChatLikedByMemberIdResponse> 실제_결과 = 응답.as(new TypeRef<>() {
        });
        assertThat(실제_결과.content()).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }

    public static void 이미_좋아요를_누름(ExtractableResponse<Response> 응답) {
        QueryChatByIdResponse 채팅 = 응답.as(QueryChatByIdResponse.class);
        assertThat(채팅.isAlreadyClickLike()).isTrue();
    }

    public static void 좋아요를_누르지_않음(ExtractableResponse<Response> 응답) {
        QueryChatByIdResponse 채팅 = 응답.as(QueryChatByIdResponse.class);
        assertThat(채팅.isAlreadyClickLike()).isFalse();
    }
}
