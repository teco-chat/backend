package chat.teco.tecochat.acceptance.comment;

import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_수정_요청;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_작성_요청;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_제거_요청;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.채팅에_달린_댓글들_조회_요청;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.member.domain.Course;
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
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CommentController 인수 테스트")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CommentAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Chat chat;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        회원_가입_요청("말랑", Course.BACKEND);
        chat = chatRepository.save(ChatFixture.defaultChat());
    }

    @Test
    void 댓글을_단다() {
        // when
        final ExtractableResponse<Response> response = 댓글_작성_요청("말랑", chat.id(), "댓글 내용입니다.");

        // then
        final String location = response.header("location");
        final String id = location.substring(location.lastIndexOf("/") + 1);
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo(id);
        assertThat(response.header("location")).contains("/comments/");
    }

    @Test
    void 채팅이_없는데_댓글을_달려고_하면_예외() {
        // when
        final ExtractableResponse<Response> response =
                댓글_작성_요청("말랑", chat.id() + 1, "댓글 내용입니다.");

        // then
        assertThat(response.statusCode()).isEqualTo(NOT_FOUND.value());
    }

    @Test
    void 댓글을_이어서_단다() {
        // given
        댓글을_단다();

        // when & then
        댓글을_단다();
    }

    @Test
    void 댓글을_수정한다() {
        // given
        댓글_작성_요청("말랑", chat.id(), "댓글 내용입니다.");

        // when
        final ExtractableResponse<Response> response = 댓글_수정_요청("말랑", chat.id(), "변경합니다");

        // then
        assertThat(response.statusCode()).isEqualTo(OK.value());
        final Comment comment = commentRepository.findById(1L).get();
        assertThat(comment.content()).isEqualTo("변경합니다");
    }

    @Test
    void 댓글을_제거한다() {
        // given
        댓글_작성_요청("말랑", chat.id(), "댓글 내용입니다.");

        // when
        댓글_제거_요청("말랑", 1L);

        // then
        assertThat(commentRepository.findById(1L)).isEmpty();
    }

    @Test
    void 본인의_댓글이_아니면_제거할_수_없다() {
        // given
        회원_가입_요청("허브", Course.FRONTEND);
        댓글_작성_요청("말랑", chat.id(), "댓글 내용입니다.");

        // when
        final ExtractableResponse<Response> response = 댓글_제거_요청("허브", 1L);

        // then
        assertThat(response.statusCode()).isEqualTo(FORBIDDEN.value());
        assertThat(commentRepository.findById(1L)).isPresent();
    }

    @Test
    void 채팅에_달린_댓글들을_조회한다() {
        // given
        회원_가입_요청("허브", Course.FRONTEND);
        댓글_작성_요청("말랑", chat.id(), "나는 말랑");
        댓글_작성_요청("허브", chat.id(), "나는 허브");

        // when & then
        final ExtractableResponse<Response> response = 채팅에_달린_댓글들_조회_요청(chat.id());
        assertThat(response.jsonPath().getString("[0].crewName")).isEqualTo("말랑");
        assertThat(response.jsonPath().getString("[0].content")).isEqualTo("나는 말랑");
        assertThat(response.jsonPath().getString("[0].course")).isEqualTo("BACKEND");
        assertThat(response.jsonPath().getString("[1].crewName")).isEqualTo("허브");
        assertThat(response.jsonPath().getString("[1].content")).isEqualTo("나는 허브");
        assertThat(response.jsonPath().getString("[1].course")).isEqualTo("FRONTEND");
    }
}
