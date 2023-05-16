package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.chat.ChatBuilder.chatBuilder;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.GPT_의_응답을_지정한다;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.N번째_질문_혹은_답변_내용_검증;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.이름_과정_제목으로_검색_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.질문에_대해_응답됨;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅_이어하기_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅의_정보가_조회된다;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅의_키워드_검증;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.첫_채팅_요청;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.GptClient;
import chat.teco.tecochat.chat.dto.ChatQueryDto.KeywordQueryDto;
import chat.teco.tecochat.chat.dto.ChatSearchQueryDto;
import chat.teco.tecochat.common.presentation.PageResponse;
import chat.teco.tecochat.member.domain.Course;
import io.restassured.RestAssured;
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
import org.springframework.core.ParameterizedTypeReference;
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
        chatBuilder(gptClient)
                .첫_질문_요청("말랑", "안녕?")
                .GPT_의_답변_세팅("응 안녕")
                .키워드_세팅("인사", "반가움", "안녕")
                .요청을_보낸다();

        // when
        final ExtractableResponse<Response> response = 단일_채팅_조회_요청(1L, "말랑");

        // then
        채팅의_정보가_조회된다(response, 1L, "말랑", "BACKEND", "안녕?");
        채팅의_키워드_검증(response, "인사", "반가움", "안녕");
        N번째_질문_혹은_답변_내용_검증(response, 0, "안녕?", "user");
        N번째_질문_혹은_답변_내용_검증(response, 1, "응 안녕", "assistant");
    }

    @Test
    void 이름_과정_제목으로_검색한다() {
        // given
        회원_가입_요청("안드로이드 허브", Course.ANDROID);
        회원_가입_요청("프론트 허브", Course.FRONTEND);
        회원_가입_요청("프론트2 허브", Course.FRONTEND);

        chatBuilder(gptClient)
                .첫_질문_요청("안드로이드 허브", "안녕?")
                .GPT_의_답변_세팅("응 안녕")
                .키워드_세팅("인사 안드로이드", "반가움", "안녕")
                .요청을_보낸다();

        chatBuilder(gptClient)
                .첫_질문_요청("프론트 허브", "안녕?")
                .GPT_의_답변_세팅("응 안녕")
                .키워드_세팅("인사", "반가움 허브", "안녕")
                .요청을_보낸다();

        비동기_처리를_위해_잠깐_멈춘다();

        chatBuilder(gptClient)
                .첫_질문_요청("프론트2 허브", "안녕?")
                .GPT_의_답변_세팅("응 안녕")
                .키워드_세팅("인사", "반가움", "안녕 프론트2")
                .요청을_보낸다();

        // when & then
        final ExtractableResponse<Response> response = 이름_과정_제목으로_검색_요청("허브", Course.FRONTEND, "안녕");
        final PageResponse<ChatSearchQueryDto> chatSearchQueryDtos = response.as(
                new ParameterizedTypeReference<PageResponse<ChatSearchQueryDto>>() {
                }.getType());
        final ChatSearchQueryDto 첫번째_결과 = chatSearchQueryDtos.content().get(0);
        final ChatSearchQueryDto 두번째_결과 = chatSearchQueryDtos.content().get(1);
        assertThat(첫번째_결과.crewName()).isEqualTo("프론트2 허브");
        assertThat(첫번째_결과.course().name()).isEqualTo("FRONTEND");
        assertThat(첫번째_결과.totalQnaCount()).isEqualTo(1);
        assertThat(첫번째_결과.keywords())
                .extracting(KeywordQueryDto::keyword)
                .containsExactly("인사", "반가움", "안녕 프론트2");
        assertThat(두번째_결과.crewName()).isEqualTo("프론트 허브");
        assertThat(두번째_결과.course().name()).isEqualTo("FRONTEND");
        assertThat(두번째_결과.totalQnaCount()).isEqualTo(1);
        assertThat(두번째_결과.keywords())
                .extracting(KeywordQueryDto::keyword)
                .containsExactly("인사", "반가움 허브", "안녕");
        assertThat(chatSearchQueryDtos.totalElements()).isEqualTo(2);
    }

    private static void 비동기_처리를_위해_잠깐_멈춘다() {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
