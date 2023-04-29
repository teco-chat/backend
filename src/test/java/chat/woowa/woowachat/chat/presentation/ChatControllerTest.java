package chat.woowa.woowachat.chat.presentation;

import static chat.woowa.woowachat.chat.domain.Answer.answer;
import static chat.woowa.woowachat.chat.domain.Question.question;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.Question;
import chat.woowa.woowachat.chat.domain.QuestionAndAnswer;
import chat.woowa.woowachat.chat.dto.AskRequest;
import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.response.ValidatableResponse;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;


@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatController 은(는)")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ChatControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GptClient gptClient;

    private static void validateMessage(final ValidatableResponse validatableResponse,
                                        final int index,
                                        final String content,
                                        final String role) {
        validatableResponse
                .body("messages[" + index + "].content", equalTo(content))
                .body("messages[" + index + "].role", equalTo(role))
                .body("messages[" + index + "].createdAt", notNullValue());
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        memberRepository.save(new Member("말랑", Course.BACKEND)).id();
    }

    @Test
    void 첫_질문을_한다() throws Exception {
        // given
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(new QuestionAndAnswer(
                        question("안녕?"),
                        answer("응 안녕"),
                        10
                ));
        final AskRequest askRequest = new AskRequest("안녕?");
        final String body = objectMapper.writeValueAsString(askRequest);

        // when & then
        RestAssured.given().log().all()
                .header(new Header("name", encode("말랑")))
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/chats")
                .then()
                .log().all()
                .statusCode(CREATED.value())
                .body("content", equalTo("응 안녕"));
    }

    @Test
    void 질문을_이어서_한다() throws Exception {
        // given
        첫_질문을_한다();
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(new QuestionAndAnswer(
                        question("안녕? 2"),
                        answer("응 안녕 2"),
                        10
                ));
        final AskRequest askRequest = new AskRequest("안녕? 2");
        final String body = objectMapper.writeValueAsString(askRequest);

        // when & then
        RestAssured.given().log().all()
                .header(new Header("name", encode("말랑")))
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/chats/" + 1L)
                .then()
                .log().all()
                .statusCode(CREATED.value())
                .body("content", equalTo("응 안녕 2"));
    }

    @Test
    void 채팅_기록을_조회한다() throws Exception {
        // given
        질문을_이어서_한다();

        // when
        final ValidatableResponse validatableResponse = RestAssured.given()
                .log().all()
                .when()
                .get("/chats/" + 1L)
                .then()
                .log().all()
                .statusCode(OK.value());

        // then
        validatableResponse
                .body("id", equalTo(1))
                .body("crewName", equalTo("말랑"))
                .body("course", equalTo("BACKEND"))
                .body("title", equalTo("안녕?"))
                .body("createdAt", notNullValue());
        assertAll(
                () -> validateMessage(validatableResponse, 0, "안녕?", "user"),
                () -> validateMessage(validatableResponse, 1, "응 안녕", "assistant"),
                () -> validateMessage(validatableResponse, 2, "안녕? 2", "user"),
                () -> validateMessage(validatableResponse, 3, "응 안녕 2", "assistant")
        );
    }

    @Test
    void 이름_과정_제목으로_검색한다() throws Exception {
        // given
        질문을_한다("안드로이드 허브", Course.ANDROID);
        질문을_한다("프론트 허브", Course.FRONTEND);
        질문을_한다("프론트2 허브", Course.FRONTEND);

        // when & then
        RestAssured.given()
                .log().all()
                .when()
                .get("/chats?name=허브&course=FRONTEND")
                .then()
                .body("content[0].crewName", equalTo("프론트2 허브"))
                .body("content[0].course", equalTo("FRONTEND"))
                .body("content[0].totalQnaCount", equalTo(1))
                .body("content[1].crewName", equalTo("프론트 허브"))
                .body("content[1].course", equalTo("FRONTEND"))
                .body("content[1].totalQnaCount", equalTo(1))
                .body("totalElements", equalTo(2))
                .log().all();
    }

    private void 질문을_한다(final String name, final Course course) throws Exception {
        memberRepository.save(new Member(name, course)).id();
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(new QuestionAndAnswer(
                        question("안녕?"),
                        answer("응 안녕"),
                        10
                ));
        final AskRequest askRequest = new AskRequest("안녕?");
        final String body = objectMapper.writeValueAsString(askRequest);

        // when & then
        RestAssured.given()
                .header(new Header("name", encode(name)))
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/chats");
    }

    private String encode(final String name) {
        return new String(Base64.getEncoder().encode(name.getBytes()));
    }
}
