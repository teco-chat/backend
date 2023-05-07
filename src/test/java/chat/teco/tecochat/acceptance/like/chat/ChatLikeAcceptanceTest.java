package chat.teco.tecochat.acceptance.like.chat;

import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.좋아요_요청;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.채팅에_달린_좋아요_조회_요청;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.회원이_누른_좋아요_조회_요청;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Course;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
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
@DisplayName("ChatLikeController 인수 테스트")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChatLikeAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatLikeRepository chatLikeRepository;

    private Chat 말랑채팅;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        회원_가입_요청("말랑", Course.BACKEND);
        말랑채팅 = chatRepository.save(ChatFixture.defaultChat("말랑이 채팅", 1L));
    }

    @Test
    void 채팅에_좋아요를_누른다() {
        // when
        final ExtractableResponse<Response> response = 좋아요_요청("말랑", 말랑채팅.id());

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(chatLikeRepository.findAll()).hasSize(1);
    }

    @Test
    void 좋아요를_두번_눌러서_기존에_누른_좋아요를_제거한다() {
        // given
        좋아요_요청("말랑", 말랑채팅.id());

        // when
        final ExtractableResponse<Response> response = 좋아요_요청("말랑", 말랑채팅.id());

        // then
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(chatLikeRepository.findAll()).hasSize(0);
    }

    @Test
    void 채팅에_달린_좋아요들을_조회한다() {
        // given
        회원_가입_요청("허브", Course.FRONTEND);
        회원_가입_요청("박스터", Course.ANDROID);
        좋아요_요청("말랑", 말랑채팅.id());
        좋아요_요청("허브", 말랑채팅.id());
        좋아요_요청("박스터", 말랑채팅.id());

        // when
        final ExtractableResponse<Response> response = 채팅에_달린_좋아요_조회_요청(말랑채팅.id());

        // then
        final JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getInt("size()")).isEqualTo(3);
        assertThat(jsonPath.getString("[0].id")).isEqualTo("3");
        assertThat(jsonPath.getString("[0].memberInfo.crewName")).isEqualTo("박스터");
        assertThat(jsonPath.getString("[0].memberInfo.course")).isEqualTo("ANDROID");
        assertThat(jsonPath.getString("[1].id")).isEqualTo("2");
        assertThat(jsonPath.getString("[1].memberInfo.crewName")).isEqualTo("허브");
        assertThat(jsonPath.getString("[1].memberInfo.course")).isEqualTo("FRONTEND");
        assertThat(jsonPath.getString("[2].id")).isEqualTo("1");
        assertThat(jsonPath.getString("[2].memberInfo.crewName")).isEqualTo("말랑");
        assertThat(jsonPath.getString("[2].memberInfo.course")).isEqualTo("BACKEND");
    }

    @Test
    void 내가_좋아요를_단_채팅을_조회한다() {
        // given
        회원_가입_요청("허브", Course.FRONTEND);
        회원_가입_요청("박스터", Course.ANDROID);
        final Chat 허브채팅1 = chatRepository.save(ChatFixture.defaultChat("허브채팅1", 2L));
        final Chat 허브채팅2 = chatRepository.save(ChatFixture.defaultChat("허브채팅2", 2L));
        좋아요_요청("말랑", 허브채팅2.id());
        좋아요_요청("말랑", 허브채팅1.id());
        좋아요_요청("말랑", 말랑채팅.id());

        // when
        final ExtractableResponse<Response> response = 회원이_누른_좋아요_조회_요청("말랑");

        // then
        final JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getInt("size()")).isEqualTo(3);

        assertThat(jsonPath.getLong("[0].id")).isEqualTo(3);
        assertThat(jsonPath.getLong("[0].chatInfo.id")).isEqualTo(1);
        assertThat(jsonPath.getLong("[0].chatInfo.crewId")).isEqualTo(1);
        assertThat(jsonPath.getString("[0].chatInfo.crewName")).isEqualTo("말랑");
        assertThat(jsonPath.getString("[0].chatInfo.course")).isEqualTo("BACKEND");
        assertThat(jsonPath.getString("[0].chatInfo.title")).isEqualTo("말랑이 채팅");
        assertThat(jsonPath.getInt("[0].chatInfo.likeCount")).isEqualTo(1);
        assertThat(jsonPath.getInt("[0].chatInfo.totalQnaCount")).isEqualTo(0);

        assertThat(jsonPath.getLong("[1].id")).isEqualTo(2);
        assertThat(jsonPath.getLong("[1].chatInfo.id")).isEqualTo(2);
        assertThat(jsonPath.getLong("[1].chatInfo.crewId")).isEqualTo(2);
        assertThat(jsonPath.getString("[1].chatInfo.crewName")).isEqualTo("허브");
        assertThat(jsonPath.getString("[1].chatInfo.course")).isEqualTo("FRONTEND");
        assertThat(jsonPath.getString("[1].chatInfo.title")).isEqualTo("허브채팅1");
        assertThat(jsonPath.getInt("[1].chatInfo.likeCount")).isEqualTo(1);
        assertThat(jsonPath.getInt("[1].chatInfo.totalQnaCount")).isEqualTo(0);

        assertThat(jsonPath.getLong("[2].id")).isEqualTo(1);
        assertThat(jsonPath.getLong("[2].chatInfo.id")).isEqualTo(3);
        assertThat(jsonPath.getLong("[2].chatInfo.crewId")).isEqualTo(2);
        assertThat(jsonPath.getString("[2].chatInfo.crewName")).isEqualTo("허브");
        assertThat(jsonPath.getString("[2].chatInfo.course")).isEqualTo("FRONTEND");
        assertThat(jsonPath.getString("[2].chatInfo.title")).isEqualTo("허브채팅2");
        assertThat(jsonPath.getInt("[2].chatInfo.likeCount")).isEqualTo(1);
        assertThat(jsonPath.getInt("[2].chatInfo.totalQnaCount")).isEqualTo(0);
    }
}
