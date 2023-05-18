package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.chat.ChatSteps.검색_결과_없음;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.검색_결과의_내용_검증;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_결과를_확인한다;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.이름_과정_제목으로_검색_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅_이어하기_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.첫_채팅_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.첫_채팅_요청후_ID_반환;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.첫_채팅의_응답을_확인한다;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.비어있음;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.요청_결과의_상태를_검증한다;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.정상_생성;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static chat.teco.tecochat.chat.fixture.ChatFixture.검색시_조회될_채팅_키워드;
import static chat.teco.tecochat.chat.fixture.ChatFixture.단일_채팅_조회의_예상_결과;
import static chat.teco.tecochat.chat.fixture.ChatFixture.단일_채팅_키워드;
import static chat.teco.tecochat.chat.fixture.ChatFixture.대화_내용;
import static chat.teco.tecochat.chat.fixture.ChatFixture.채팅_검색의_예상_결과;
import static chat.teco.tecochat.chat.fixture.ChatFixture.채팅_검색의_예상_결과들;
import static chat.teco.tecochat.member.domain.Course.ANDROID;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.mockito.BDDMockito.reset;

import chat.teco.tecochat.chat.application.chat.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.chat.domain.chat.GptClient;
import io.restassured.RestAssured;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
    }

    @AfterEach
    void tearDown() {
        reset(gptClient);
    }

    @Test
    void 첫_질문을_한다() {
        // given
        회원_가입_요청("말랑", BACKEND);

        // when
        var 응답 = 첫_채팅_요청(gptClient, "말랑", "안녕?", "응 안녕", "인사", "안녕", "키워드");

        // then
        요청_결과의_상태를_검증한다(응답, 정상_생성);
        첫_채팅의_응답을_확인한다(응답, "응 안녕");
    }

    @Test
    void 질문을_이어서_한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "안녕?", "응 안녕?");

        // when
        var 응답 = 채팅_이어하기_요청(gptClient, "말랑", "안녕? 2", "응 안녕 2", 채팅_ID);

        // then
        요청_결과의_상태를_검증한다(응답, 정상_생성);
        첫_채팅의_응답을_확인한다(응답, "응 안녕 2");
    }

    @Test
    void 채팅_기록을_조회한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "안녕?", "응 안녕?", "인사", "반가움", "안녕");

        // when
        var 응답 = 단일_채팅_조회_요청(채팅_ID, "말랑");

        // then
        var 단일_채팅_조회의_예상_결과 = 단일_채팅_조회의_예상_결과(
                1L,
                "말랑",
                BACKEND,
                "안녕?",
                0,
                false,
                대화_내용("안녕?", "응 안녕?"),
                단일_채팅_키워드("인사", "반가움", "안녕")
        );
        단일_채팅_조회_결과를_확인한다(응답, 단일_채팅_조회의_예상_결과);
    }

    @Test
    void 이름_과정_제목으로_검색한다() {
        // given
        회원_가입_요청("안드로이드 말랑", ANDROID);
        회원_가입_요청("프론트 말랑", FRONTEND);
        회원_가입_요청("프론트2 말랑", FRONTEND);

        첫_채팅_요청(gptClient, "안드로이드 말랑", "안녕1?", "응 안녕1");
        첫_채팅_요청(gptClient, "프론트 말랑", "안녕2?", "응 안녕2");
        첫_채팅_요청(gptClient, "프론트2 말랑", "안녕3?", "응 안녕3");

        // when & then
        var 검색_응답 = 이름_과정_제목으로_검색_요청("말랑", FRONTEND, "안녕");

        var 예상_채팅_내용들 = 채팅_검색의_예상_결과들(
                채팅_검색의_예상_결과(
                        3L,
                        3L,
                        "프론트2 말랑",
                        FRONTEND,
                        "안녕3?",
                        0,
                        1,
                        비어있음()),
                채팅_검색의_예상_결과(
                        2L,
                        2L,
                        "프론트 말랑",
                        FRONTEND,
                        "안녕2?",
                        0,
                        1,
                        비어있음())
        );
        검색_결과의_내용_검증(검색_응답, 예상_채팅_내용들);
    }

    @Test
    void 검색시_키워드도_함께_조회된다() {
        // given
        회원_가입_요청("안드로이드 말랑", ANDROID);

        첫_채팅_요청(gptClient, "안드로이드 말랑", "안녕?", "응 안녕", "안드로이드", "인사", "안부");

        // when & then
        var 검색_응답 = 이름_과정_제목으로_검색_요청("말랑", ANDROID, "안녕");

        List<SearchChatResponse> 예상_채팅_내용들 = new ArrayList<>();

        예상_채팅_내용들.add(채팅_검색의_예상_결과(
                1L,
                1L,
                "안드로이드 말랑",
                ANDROID,
                "안녕?",
                0,
                1,
                검색시_조회될_채팅_키워드("안드로이드", "인사", "안부")));
        검색_결과의_내용_검증(검색_응답, 예상_채팅_내용들);
    }

    @Test
    void 이름_과정_제목으로_검색한다_조건_일치_X() {
        // given
        회원_가입_요청("안드로이드 허브", ANDROID);

        첫_채팅_요청(gptClient, "안드로이드 허브", "안녕?", "응 안녕");

        // when & then
        var 검색_결과 = 이름_과정_제목으로_검색_요청("허브_좋아요", FRONTEND, "매치안됨");
        검색_결과_없음(검색_결과);
    }
}
