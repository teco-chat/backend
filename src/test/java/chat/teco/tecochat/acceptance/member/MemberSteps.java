package chat.teco.tecochat.acceptance.member;

import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.given;

import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.presentation.request.SignUpRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class MemberSteps {

    public static ExtractableResponse<Response> 회원_가입_요청(String 이름, Course 과정) {
        SignUpRequest dto = new SignUpRequest(이름, 과정);
        return given()
                .body(dto)
                .when()
                .post("/members")
                .then()
                .log().all()
                .extract();
    }
}
