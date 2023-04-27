package chat.woowa.woowachat.chat.presentation;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.Message;
import chat.woowa.woowachat.chat.dto.AskRequest;
import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.Header;
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

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        memberRepository.save(new Member("말랑", Course.BACKEND)).id();
    }

    @Test
    void 첫_질문을_한다() throws Exception {
        // given
        given(gptClient.ask(any()))
                .willReturn(Message.assistant("응 안녕", 10));
        final AskRequest askRequest = new AskRequest("안녕?", 50);
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
        given(gptClient.ask(any()))
                .willReturn(Message.assistant("응 안녕 두번째", 10));
        final AskRequest askRequest = new AskRequest("안녕? 두번째", 50);
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
                .body("content", equalTo("응 안녕 두번째"));
    }

    @Test
    void 채팅_기록을_조회한다() throws Exception {
        // given
        질문을_이어서_한다();

        // when
        RestAssured.given().log().all()
                .when()
                .get("/chats/" + 1L)
                .then()
                .log().all()
                .statusCode(OK.value())
                .body("id", equalTo(1))
                .body("crewName", equalTo("말랑"))
                .body("course", equalTo("BACKEND"))
                .body("title", equalTo("안녕?"))
                .body("createdAt", notNullValue())

                .body("messages[0].id", equalTo(1))
                .body("messages[0].content", equalTo("안녕?"))
                .body("messages[0].role", equalTo("user"))
                .body("messages[0].createdAt", notNullValue())

                .body("messages[1].id", equalTo(2))
                .body("messages[1].content", equalTo("응 안녕"))
                .body("messages[1].role", equalTo("assistant"))
                .body("messages[1].createdAt", notNullValue())

                .body("messages[2].id", equalTo(3))
                .body("messages[2].content", equalTo("안녕? 두번째"))
                .body("messages[2].role", equalTo("user"))
                .body("messages[2].createdAt", notNullValue())

                .body("messages[3].id", equalTo(4))
                .body("messages[3].content", equalTo("응 안녕 두번째"))
                .body("messages[3].role", equalTo("assistant"))
                .body("messages[3].createdAt", notNullValue());
    }

    private String encode(final String name) {
        return new String(Base64.getEncoder().encode(name.getBytes()));
    }
}
