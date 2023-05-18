package chat.teco.tecochat.acceptance.comment;

import static chat.teco.tecochat.acceptance.chat.ChatSteps.첫_채팅_요청후_ID_반환;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_수정_요청;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_작성_요청;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_작성후_댓글_ID_반환;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글_제거_요청;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.댓글들_조회_내용_검증;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.생성된_댓글의_ID;
import static chat.teco.tecochat.acceptance.comment.CommentSteps.채팅에_달린_댓글들_조회_요청;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.권한_없음;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.비어있음;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.요청_결과의_상태를_검증한다;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.정상_생성;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.정상_요청;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.찾을수_없음;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static chat.teco.tecochat.comment.fixture.CommentFixture.댓글_검색의_예상_결과;
import static chat.teco.tecochat.comment.fixture.CommentFixture.댓글_검색의_예상_결과들;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.reset;

import chat.teco.tecochat.chat.domain.chat.GptClient;
import io.restassured.RestAssured;
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
@DisplayName("CommentController 인수 테스트")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class CommentAcceptanceTest {

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
    void 댓글을_단다() {
        // when
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "질문1", "답변1");
        var 응답 = 댓글_작성_요청("말랑", 채팅_ID, "댓글 내용입니다.");

        // then
        요청_결과의_상태를_검증한다(응답, 정상_생성);
        Long 댓글_ID = 생성된_댓글의_ID(응답);
        assertThat(댓글_ID).isNotNull();
    }

    @Test
    void 채팅이_없는데_댓글을_달려고_하면_예외() {
        // when
        회원_가입_요청("말랑", BACKEND);
        var 응답 = 댓글_작성_요청("말랑", 1L, "댓글 내용입니다.");

        // then
        요청_결과의_상태를_검증한다(응답, 찾을수_없음);
    }

    @Test
    void 댓글을_이어서_단다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "질문1", "답변1");
        댓글_작성_요청("말랑", 채팅_ID, "1 댓글 내용입니다.");
        var 응답 = 댓글_작성_요청("말랑", 채팅_ID, "2 댓글 내용입니다.");

        // when & then
        요청_결과의_상태를_검증한다(응답, 정상_생성);
        Long 댓글_ID = 생성된_댓글의_ID(응답);
        assertThat(댓글_ID).isNotNull();
    }

    @Test
    void 댓글을_수정한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "질문1", "답변1");
        Long 댓글_ID = 댓글_작성후_댓글_ID_반환("말랑", 채팅_ID, "댓글 내용입니다.");

        // when
        var 응답 = 댓글_수정_요청("말랑", 댓글_ID, "변경합니다");

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 조회_응답 = 채팅에_달린_댓글들_조회_요청(채팅_ID);
        var 예상_결과 = 댓글_검색의_예상_결과들(
                댓글_검색의_예상_결과(댓글_ID, "말랑", BACKEND, "변경합니다")
        );
        댓글들_조회_내용_검증(조회_응답, 예상_결과);
    }

    @Test
    void 댓글을_제거한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "질문1", "답변1");
        Long 댓글_ID = 댓글_작성후_댓글_ID_반환("말랑", 채팅_ID, "댓글 내용입니다.");

        // when
        var 응답 = 댓글_제거_요청("말랑", 댓글_ID);

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 조회_응답 = 채팅에_달린_댓글들_조회_요청(채팅_ID);
        댓글들_조회_내용_검증(조회_응답, 비어있음());
    }

    @Test
    void 본인의_댓글이_아니면_제거할_수_없다() {
        // given
        회원_가입_요청("허브", FRONTEND);
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "질문1", "답변1");
        Long 댓글_ID = 댓글_작성후_댓글_ID_반환("말랑", 채팅_ID, "댓글 내용입니다.");

        // when
        var 응답 = 댓글_제거_요청("허브", 댓글_ID);

        // then
        요청_결과의_상태를_검증한다(응답, 권한_없음);
        var 조회_응답 = 채팅에_달린_댓글들_조회_요청(채팅_ID);
        var 예상_결과 = 댓글_검색의_예상_결과들(
                댓글_검색의_예상_결과(댓글_ID, "말랑", BACKEND, "댓글 내용입니다.")
        );
        댓글들_조회_내용_검증(조회_응답, 예상_결과);
    }

    @Test
    void 채팅에_달린_댓글들을_조회한다() {
        // given
        회원_가입_요청("허브", FRONTEND);
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 첫_채팅_요청후_ID_반환(gptClient, "말랑", "질문1", "답변1");
        Long 댓글1_ID = 댓글_작성후_댓글_ID_반환("말랑", 채팅_ID, "댓글 내용입니다.");
        Long 댓글2_ID = 댓글_작성후_댓글_ID_반환("말랑", 채팅_ID, "나는 말랑");
        Long 댓글3_ID = 댓글_작성후_댓글_ID_반환("허브", 채팅_ID, "나는 허브");

        // when & then
        var 응답 = 채팅에_달린_댓글들_조회_요청(채팅_ID);
        var 예상_결과 = 댓글_검색의_예상_결과들(
                댓글_검색의_예상_결과(댓글1_ID, "말랑", BACKEND, "댓글 내용입니다."),
                댓글_검색의_예상_결과(댓글2_ID, "말랑", BACKEND, "나는 말랑"),
                댓글_검색의_예상_결과(댓글3_ID, "허브", FRONTEND, "나는 허브")
        );
        댓글들_조회_내용_검증(응답, 예상_결과);
    }
}
