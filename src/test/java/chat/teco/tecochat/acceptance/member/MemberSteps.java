package chat.teco.tecochat.acceptance.member;

import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;

import chat.teco.tecochat.acceptance.AcceptanceTestSteps;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.dto.SignUpDto;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

@SuppressWarnings("NonAsciiCharacters")
public class MemberSteps {

    public static ExtractableResponse<Response> 회원_가입_요청(final String name, final Course course) {
        final SignUpDto dto = new SignUpDto(name, course);
        return AcceptanceTestSteps.given()
                .body(toJson(dto))
                .when()
                .post("/members")
                .then()
                .log().all()
                .extract();
    }
}
