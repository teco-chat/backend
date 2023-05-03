package chat.teco.tecochat.member.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.dto.SignUpDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
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
@DisplayName("MemberController 은(는)")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 회원_가입을_진행한다() throws Exception {
        // given
        final SignUpDto dto = new SignUpDto("우가", Course.BACKEND);
        final String body = objectMapper.writeValueAsString(dto);

        // when
        RestAssured.given().log().all()
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/members")
                .then()
                .log().all()
                .statusCode(CREATED.value()).extract();

        // then
        assertThat(memberRepository.findAll()).hasSize(1);
    }
}