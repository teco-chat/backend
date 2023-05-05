package chat.teco.tecochat.acceptance.comment;

import static chat.teco.tecochat.acceptance.AcceptanceTestSteps.given;
import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;

import chat.teco.tecochat.comment.presentation.request.UpdateCommentRequest;
import chat.teco.tecochat.comment.presentation.request.WriteCommentRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
public class CommentSteps {

    public static ExtractableResponse<Response> 댓글_작성_요청(final String name, final Long chatId, final String content) {
        final WriteCommentRequest request = new WriteCommentRequest(chatId, content);
        return given(name)
                .body(toJson(request))
                .when()
                .post("/comments")
                .then()
                .log().all()
                .extract();
    }

    @Test
    public static ExtractableResponse<Response> 댓글_수정_요청(
            final String name,
            final Long commentId,
            final String content
    ) {
        final UpdateCommentRequest request = new UpdateCommentRequest(content);
        return given(name).body(toJson(request))
                .when()
                .patch("/comments/{id}", commentId)
                .then()
                .log().all()
                .extract();
    }

    @Test
    public static ExtractableResponse<Response> 댓글_제거_요청(final String name, final Long commentId) {
        return given(name)
                .when()
                .delete("/comments/{id}", commentId)
                .then()
                .log().all()
                .extract();
    }

    @Test
    public static ExtractableResponse<Response> 채팅에_달린_댓글들_조회_요청(final Long chatId) {
        return RestAssured.given().log().all()
                .when()
                .get("/comments?chatId=" + chatId)
                .then()
                .log().all()
                .extract();
    }
}
