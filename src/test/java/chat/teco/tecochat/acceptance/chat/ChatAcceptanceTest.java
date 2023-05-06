package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.chat.ChatSteps.GPT_의_응답을_지정한다;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.이름_과정_제목으로_검색_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.질문에_대해_응답됨;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅_이어하기_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.첫_채팅_요청;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.GptClient;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatController 인수 테스트")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class ChatAcceptanceTest {

    @LocalServerPort
    private int port;

    @MockBean
    private GptClient gptClient;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        회원_가입_요청("말랑", Course.BACKEND);
    }

    @Test
    void 첫_질문을_한다() {
        // given
        GPT_의_응답을_지정한다(gptClient, "안녕?", "응 안녕", 10);

        // when
        final ExtractableResponse<Response> response = 첫_채팅_요청("말랑", "안녕?");

        // then
        질문에_대해_응답됨(response, "응 안녕");
    }

    @Test
    void 질문을_이어서_한다() {
        // given
        첫_질문을_한다();
        GPT_의_응답을_지정한다(gptClient, "안녕? 2", "응 안녕 2", 10);

        // when
        final ExtractableResponse<Response> response = 채팅_이어하기_요청("말랑", "안녕? 2", 1L);

        // then
        질문에_대해_응답됨(response, "응 안녕 2");
    }

    @Test
    void 채팅_기록을_조회한다() {
        // given
        GPT_의_응답을_지정한다(gptClient, "안녕?", "응 안녕", 10);
        첫_채팅_요청("말랑", "안녕?");

        GPT_의_응답을_지정한다(gptClient, "안녕? 2", "응 안녕 2", 10);
        채팅_이어하기_요청("말랑", "안녕? 2", 1L);

        // when
        final ExtractableResponse<Response> response = 단일_채팅_조회_요청(1L);

        // then
        채팅의_정보가_조회된다(response.jsonPath(), 1L, "말랑", "BACKEND", "안녕?");
        N번째_질문_혹은_답변_내용_검증(response, 0, "안녕?", "user");
        N번째_질문_혹은_답변_내용_검증(response, 1, "응 안녕", "assistant");
        N번째_질문_혹은_답변_내용_검증(response, 2, "안녕? 2", "user");
        N번째_질문_혹은_답변_내용_검증(response, 3, "응 안녕 2", "assistant");
    }

    private void 채팅의_정보가_조회된다(
            final JsonPath jsonPath,
            final Long id,
            final String crewName,
            final String course,
            final String title
    ) {
        assertThat(jsonPath.getLong("id")).isEqualTo(id);
        assertThat(jsonPath.getString("crewName")).isEqualTo(crewName);
        assertThat(jsonPath.getString("course")).isEqualTo(course);
        assertThat(jsonPath.getString("title")).isEqualTo(title);
        assertThat(jsonPath.getString("createdAt")).isNotNull();
    }

    private void N번째_질문_혹은_답변_내용_검증(
            final ExtractableResponse<Response> response,
            final int index,
            final String content,
            final String role
    ) {
        final JsonPath jsonPath = response.jsonPath();
        assertThat(jsonPath.getString("messages[" + index + "].content"))
                .isEqualTo(content);
        assertThat(jsonPath.getString("messages[" + index + "].role"))
                .isEqualTo(role);
        assertThat(jsonPath.getString("messages[" + index + "].createdAt"))
                .isNotNull();
    }


    @Test
    void 이름_과정_제목으로_검색한다() {
        // given
        회원_가입_요청("안드로이드 허브", Course.ANDROID);
        회원_가입_요청("프론트 허브", Course.FRONTEND);
        회원_가입_요청("프론트2 허브", Course.FRONTEND);

        GPT_의_응답을_지정한다(gptClient, "안녕?", "응 안녕", 10);
        첫_채팅_요청("안드로이드 허브", "안녕?");

        GPT_의_응답을_지정한다(gptClient, "안녕?", "응 안녕", 10);
        첫_채팅_요청("프론트 허브", "안녕?");

        GPT_의_응답을_지정한다(gptClient, "안녕?", "응 안녕", 10);
        첫_채팅_요청("프론트2 허브", "안녕?");

        // when & then
        final ExtractableResponse<Response> response = 이름_과정_제목으로_검색_요청("허브", Course.FRONTEND, "안녕");
        assertThat(response.jsonPath().getString("content[0].crewName")).isEqualTo("프론트2 허브");
        assertThat(response.jsonPath().getString("content[0].course")).isEqualTo("FRONTEND");
        assertThat(response.jsonPath().getInt("content[0].totalQnaCount")).isEqualTo(1);
        assertThat(response.jsonPath().getString("content[1].crewName")).isEqualTo("프론트 허브");
        assertThat(response.jsonPath().getString("content[1].course")).isEqualTo("FRONTEND");
        assertThat(response.jsonPath().getInt("content[1].totalQnaCount")).isEqualTo(1);
        assertThat(response.jsonPath().getInt("totalElements")).isEqualTo(2);
    }

    @Test
    void 이름_과정_제목으로_검색한다_조건_일치_X() {
        // given
        회원_가입_요청("안드로이드 허브", Course.ANDROID);

        GPT_의_응답을_지정한다(gptClient, "안녕?", "응 안녕", 10);
        첫_채팅_요청("안드로이드 허브", "안녕?");

        // when & then
        final ExtractableResponse<Response> response = 이름_과정_제목으로_검색_요청("허브", Course.FRONTEND, "매치안됨");
        assertThat(response.jsonPath().getInt("totalElements")).isEqualTo(0);
    }
}
