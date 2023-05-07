package chat.teco.tecochat.acceptance.member;

import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.MemberRepository;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("MemberController 인수 테스트")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class MemberAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 회원_가입을_진행한다() {
        // given
        final ExtractableResponse<Response> response = 회원_가입_요청("말랑", Course.BACKEND);

        // then
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(memberRepository.findAll()).hasSize(1);
    }
}
