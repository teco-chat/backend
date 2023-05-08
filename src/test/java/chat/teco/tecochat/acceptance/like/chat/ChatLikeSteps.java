package chat.teco.tecochat.acceptance.like.chat;

import static chat.teco.tecochat.acceptance.AcceptanceTestSteps.given;
import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;

import chat.teco.tecochat.like.chatlike.presentation.request.PushChatLikeRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class ChatLikeSteps {

    public static ExtractableResponse<Response> 좋아요_요청(final String name, final Long chatId) {
        return given(name)
                .body(toJson(new PushChatLikeRequest(chatId)))
                .when()
                .post("/chat-likes")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 채팅에_달린_좋아요_조회_요청(final Long chatId) {
        return given()
                .when()
                .get("/chat-likes?chatId=" + chatId)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 회원이_누른_좋아요_조회_요청(final String name) {
        return given(name)
                .when()
                .get("/chat-likes")
                .then()
                .log().all()
                .extract();
    }
}
