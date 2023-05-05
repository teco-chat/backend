package chat.teco.tecochat.comment.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.presentation.request.UpdateCommentRequest;
import chat.teco.tecochat.comment.presentation.request.WriteCommentRequest;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
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
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CommentController 은(는)")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class CommentControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Header authHeader;

    private Chat chat;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        memberRepository.save(new Member("말랑", Course.BACKEND)).id();

        final String name = new String(Base64.getEncoder().encode("말랑".getBytes()));
        authHeader = new Header("name", name);

        chat = chatRepository.save(ChatFixture.defaultChat());
    }

    @Test
    void 댓글을_단다() throws Exception {
        // given
        final WriteCommentRequest request = new WriteCommentRequest(chat.id(), "댓글 내용입니다.");
        final String body = objectMapper.writeValueAsString(request);

        // when & then
        RestAssured.given().log().all()
                .header(authHeader)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/comments")
                .then()
                .log().all()
                .statusCode(CREATED.value())
                .header("location", containsString("/comments/"));
    }

    @Test
    void 채팅이_없는데_댓글을_달려고_하면_예외() throws Exception {
        // given
        final WriteCommentRequest request = new WriteCommentRequest(chat.id() + 1L, "댓글 내용입니다.");
        final String body = objectMapper.writeValueAsString(request);

        // when & then
        RestAssured.given().log().all()
                .header(authHeader)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .post("/comments")
                .then()
                .log().all()
                .statusCode(NOT_FOUND.value());
    }

    @Test
    void 댓글을_이어서_단다() throws Exception {
        // given
        댓글을_단다();

        // when & then
        댓글을_단다();
    }

    @Test
    void 댓글을_수정한다() throws Exception {
        // given
        final UpdateCommentRequest request = new UpdateCommentRequest("변경합니다");
        final String body = objectMapper.writeValueAsString(request);
        댓글을_단다();

        // when
        RestAssured.given().log().all()
                .header(authHeader)
                .contentType(APPLICATION_JSON_VALUE)
                .body(body)
                .when()
                .patch("/comments/" + 1L)
                .then()
                .log().all()
                .statusCode(OK.value());

        // then
        final Comment comment = commentRepository.findById(1L).get();
        assertThat(comment.content()).isEqualTo("변경합니다");
    }

    @Test
    void 댓글을_제거한다() throws Exception {
        // given
        댓글을_단다();

        // when
        RestAssured.given().log().all()
                .header(authHeader)
                .when()
                .delete("/comments/" + 1L)
                .then()
                .log().all()
                .statusCode(OK.value());

        // then
        assertThat(commentRepository.findById(1L)).isEmpty();
    }
}
