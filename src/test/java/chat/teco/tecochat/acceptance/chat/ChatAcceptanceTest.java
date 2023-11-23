package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.chat.ChatSteps.검색_결과_없음;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.검색_결과의_내용_검증;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.과정_조건;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_결과를_확인한다;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.복제된_채팅_ID_반환;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.요청_파라미터들;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.이름_과정_제목_좋아요_기간으로_검색_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.이름_조건;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.제목_조건;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.좋아요_기간_조겅;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅_복제_요청;
import static chat.teco.tecochat.acceptance.chat.ChatSteps.채팅_제목_수정_요청;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.비어있음;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.비정상_요청;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.요청_결과의_상태를_검증한다;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.정상_요청;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.좋아요_요청;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static chat.teco.tecochat.chat.fixture.ChatFixture.검색시_조회될_채팅_키워드;
import static chat.teco.tecochat.chat.fixture.ChatFixture.단일_채팅_조회의_예상_결과;
import static chat.teco.tecochat.chat.fixture.ChatFixture.단일_채팅_키워드;
import static chat.teco.tecochat.chat.fixture.ChatFixture.대화_내용;
import static chat.teco.tecochat.chat.fixture.ChatFixture.채팅_검색_결과;
import static chat.teco.tecochat.chat.fixture.ChatFixture.채팅_검색_결과들;
import static chat.teco.tecochat.chat.query.dao.ChatQueryDao.LikeCond.TODAY;
import static chat.teco.tecochat.domain.member.Course.ANDROID;
import static chat.teco.tecochat.domain.member.Course.BACKEND;
import static chat.teco.tecochat.domain.member.Course.FRONTEND;

import chat.teco.tecochat.acceptance.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatController 인수 테스트")
public class ChatAcceptanceTest extends AcceptanceTest {

    @Test
    void 채팅_제목을_수정한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 채팅_생성("말랑", "안녕?", "응 안녕?");

        // when
        var 응답 = 채팅_제목_수정_요청(채팅_ID, "말랑", "변경할 제목");

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 채팅_조회_결과 = 단일_채팅_조회_요청(채팅_ID, "말랑");
        var 단일_채팅_조회의_예상_결과 = 단일_채팅_조회의_예상_결과(
                채팅_ID,
                "말랑", BACKEND,
                "변경할 제목",
                0, false,
                대화_내용("안녕?", "응 안녕?"),
                단일_채팅_키워드()
        );
        단일_채팅_조회_결과를_확인한다(채팅_조회_결과, 단일_채팅_조회의_예상_결과);
    }

    @Test
    void 자신의_채팅이_아니면_제목_수정이_불가능() {
        // given
        회원_가입_요청("허브", BACKEND);
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 채팅_생성("말랑", "안녕?", "응 안녕?");

        // when
        var 응답 = 채팅_제목_수정_요청(채팅_ID, "허브", "변경할 제목");

        // then
        요청_결과의_상태를_검증한다(응답, 비정상_요청);
        var 채팅_조회_결과 = 단일_채팅_조회_요청(채팅_ID, "말랑");
        var 단일_채팅_조회의_예상_결과 = 단일_채팅_조회의_예상_결과(
                채팅_ID,
                "말랑", BACKEND,
                "안녕?",
                0, false,
                대화_내용("안녕?", "응 안녕?"),
                단일_채팅_키워드()
        );
        단일_채팅_조회_결과를_확인한다(채팅_조회_결과, 단일_채팅_조회의_예상_결과);
    }

    @Test
    void 상대방의_채팅을_복사하여_내_채팅으로_가져온다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        회원_가입_요청("허브", FRONTEND);
        Long 채팅_ID = 채팅_생성("말랑", "안녕?", "응 안녕?", "인사", "반가움", "안녕");
        채팅_이어하기("말랑이 첫 질문", "답변이야!", 채팅_ID);
        좋아요_요청("말랑", 채팅_ID);

        // when
        var 응답 = 채팅_복제_요청("허브", 채팅_ID);
        Long 복제된_채팅_ID = 복제된_채팅_ID_반환(응답);
        채팅_이어하기("허브 이어하기 질문", "이어하기 답변", 복제된_채팅_ID);

        // then
        var 원본_채팅_조회의_예상_결과 = 단일_채팅_조회의_예상_결과(
                채팅_ID,
                "말랑",
                BACKEND,
                "안녕?",
                1, true,
                대화_내용("안녕?", "응 안녕?", "말랑이 첫 질문", "답변이야!"),
                단일_채팅_키워드("인사", "반가움", "안녕")
        );
        var 원본 = 단일_채팅_조회_요청(채팅_ID, "말랑");
        단일_채팅_조회_결과를_확인한다(원본, 원본_채팅_조회의_예상_결과);

        var 복제본_채팅_조회의_예상_결과 = 단일_채팅_조회의_예상_결과(
                복제된_채팅_ID,
                "허브",
                FRONTEND,
                "안녕?",
                0, false,
                대화_내용("안녕?", "응 안녕?", "말랑이 첫 질문", "답변이야!", "허브 이어하기 질문", "이어하기 답변"),
                단일_채팅_키워드("인사", "반가움", "안녕")
        );
        var 복제본 = 단일_채팅_조회_요청(복제된_채팅_ID, "말랑");
        단일_채팅_조회_결과를_확인한다(복제본, 복제본_채팅_조회의_예상_결과);
    }

    @Test
    void 채팅_기록을_조회한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 채팅_생성("말랑", "안녕?", "응 안녕?", "인사", "반가움", "안녕");

        // when
        var 응답 = 단일_채팅_조회_요청(채팅_ID, "말랑");

        // then
        var 단일_채팅_조회의_예상_결과 = 단일_채팅_조회의_예상_결과(
                채팅_ID,
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

        채팅_생성("안드로이드 말랑", "안녕1?", "응 안녕1");
        채팅_생성("프론트 말랑", "안녕2?", "응 안녕2");
        채팅_생성("프론트2 말랑", "안녕3?", "응 안녕3");

        // when & then
        var 요청_파라미터들 = 요청_파라미터들();
        이름_조건(요청_파라미터들, "말랑");
        과정_조건(요청_파라미터들, FRONTEND);
        제목_조건(요청_파라미터들, "안녕");
        var 검색_결과 = 이름_과정_제목_좋아요_기간으로_검색_요청(요청_파라미터들);

        var 예상_채팅_내용들 = 채팅_검색_결과들(
                채팅_검색_결과(
                        3L,
                        3L,
                        "프론트2 말랑",
                        FRONTEND,
                        "안녕3?",
                        0,
                        0,
                        1,
                        비어있음()),
                채팅_검색_결과(
                        2L,
                        2L,
                        "프론트 말랑",
                        FRONTEND,
                        "안녕2?",
                        0,
                        0,
                        1,
                        비어있음())
        );
        검색_결과의_내용_검증(검색_결과, 예상_채팅_내용들);
    }

    @Test
    void 검색시_키워드도_함께_조회된다() {
        // given
        회원_가입_요청("안드로이드 말랑", ANDROID);

        채팅_생성("안드로이드 말랑", "안녕?", "응 안녕", "안드로이드", "인사", "안부");

        // when & then
        var 요청_파라미터들 = 요청_파라미터들();
        이름_조건(요청_파라미터들, "말랑");
        과정_조건(요청_파라미터들, ANDROID);
        제목_조건(요청_파라미터들, "안녕");
        var 검색_결과 = 이름_과정_제목_좋아요_기간으로_검색_요청(요청_파라미터들);

        var 예상_채팅_내용들 = 채팅_검색_결과들(
                채팅_검색_결과(
                        1L,
                        1L,
                        "안드로이드 말랑",
                        ANDROID,
                        "안녕?",
                        0,
                        0,
                        1,
                        검색시_조회될_채팅_키워드("안드로이드", "인사", "안부")
                )
        );
        검색_결과의_내용_검증(검색_결과, 예상_채팅_내용들);
    }

    @Test
    void 이름_과정_제목으로_검색한다_조건_일치_X() {
        // given
        회원_가입_요청("안드로이드 허브", ANDROID);

        채팅_생성("안드로이드 허브", "안녕?", "응 안녕");

        // when & then
        var 요청_파라미터들 = 요청_파라미터들();
        이름_조건(요청_파라미터들, "허브_좋아요");
        과정_조건(요청_파라미터들, FRONTEND);
        제목_조건(요청_파라미터들, "매치안됨");
        var 검색_결과 = 이름_과정_제목_좋아요_기간으로_검색_요청(요청_파라미터들);
        검색_결과_없음(검색_결과);
    }

    @Test
    void 좋아요_순으로_정렬한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        회원_가입_요청("허브", ANDROID);

        채팅_생성("말랑", "안녕?", "응 안녕");
        Long 말랑채팅_ID = 채팅_생성("말랑", "안녕? - 좋아요", "응 안녕");
        Long 허브채팅_ID = 채팅_생성("허브", "안녕? - 좋아요", "응 안녕");
        채팅_생성("허브", "안녕?", "응 안녕");

        좋아요_요청("말랑", 말랑채팅_ID);
        좋아요_요청("허브", 말랑채팅_ID);
        좋아요_요청("허브", 허브채팅_ID);

        // when & then
        var 요청_파라미터들 = 요청_파라미터들();
        좋아요_기간_조겅(요청_파라미터들, TODAY);
        var 검색_결과 = 이름_과정_제목_좋아요_기간으로_검색_요청(요청_파라미터들);
        var 예상_채팅_내용들 = 채팅_검색_결과들(
                채팅_검색_결과(
                        말랑채팅_ID,
                        1L,
                        "말랑",
                        BACKEND,
                        "안녕? - 좋아요",
                        2,
                        0,
                        1,
                        검색시_조회될_채팅_키워드()
                ),
                채팅_검색_결과(
                        허브채팅_ID,
                        2L,
                        "허브",
                        ANDROID,
                        "안녕? - 좋아요",
                        1,
                        0,
                        1,
                        검색시_조회될_채팅_키워드())
        );
        검색_결과의_내용_검증(검색_결과, 예상_채팅_내용들);
    }
}
