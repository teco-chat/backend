package chat.teco.tecochat.acceptance.like.chat;

import static chat.teco.tecochat.acceptance.chat.ChatSteps.단일_채팅_조회_요청;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.비어있음;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.요청_결과의_상태를_검증한다;
import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.정상_요청;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.이미_좋아요를_누름;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.좋아요_요청;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.좋아요_조회_결과_검증;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.좋아요를_누르지_않음;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.채팅에_달린_좋아요_조회_요청;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.회원이_좋아요_누른_채팅_조회_결과_검증;
import static chat.teco.tecochat.acceptance.like.chat.ChatLikeSteps.회원이_좋아요_누른_채팅_조회_요청;
import static chat.teco.tecochat.acceptance.member.MemberSteps.회원_가입_요청;
import static chat.teco.tecochat.domain.member.Course.ANDROID;
import static chat.teco.tecochat.domain.member.Course.BACKEND;
import static chat.teco.tecochat.domain.member.Course.FRONTEND;
import static chat.teco.tecochat.like.chatlike.fixture.LikeFixture.내가_좋아요_누른_채팅_조회_결과;
import static chat.teco.tecochat.like.chatlike.fixture.LikeFixture.내가_좋아요_누른_채팅_조회_결과들;
import static chat.teco.tecochat.like.chatlike.fixture.LikeFixture.조회될_채팅_키워드;
import static chat.teco.tecochat.like.chatlike.fixture.LikeFixture.채팅에_달린_좋아요_조회_예상_결과;
import static chat.teco.tecochat.like.chatlike.fixture.LikeFixture.채팅에_달린_좋아요_조회_예상_결과들;

import chat.teco.tecochat.acceptance.common.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatLikeController 인수 테스트")
public class ChatLikeAcceptanceTest extends AcceptanceTest {

    @Test
    void 채팅에_좋아요를_누른다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 채팅_생성("말랑", "질문", "답변");

        // when
        var 응답 = 좋아요_요청("말랑", 채팅_ID);

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 좋아요_조회_응답 = 채팅에_달린_좋아요_조회_요청(채팅_ID);
        var 예상_결과 = 채팅에_달린_좋아요_조회_예상_결과들(
                채팅에_달린_좋아요_조회_예상_결과("말랑", BACKEND)
        );
        좋아요_조회_결과_검증(좋아요_조회_응답, 예상_결과);
    }

    @Test
    void 좋아요를_두번_눌러서_기존에_누른_좋아요를_제거한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 채팅_ID = 채팅_생성("말랑", "질문", "답변");
        좋아요_요청("말랑", 채팅_ID);

        // when
        var 응답 = 좋아요_요청("말랑", 채팅_ID);

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 좋아요_조회_응답 = 채팅에_달린_좋아요_조회_요청(채팅_ID);
        좋아요_조회_결과_검증(좋아요_조회_응답, 비어있음());
    }

    @Test
    void 채팅에_달린_좋아요들을_조회한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        회원_가입_요청("허브", FRONTEND);
        회원_가입_요청("박스터", ANDROID);
        Long 채팅_ID = 채팅_생성("말랑", "질문", "답변");

        좋아요_요청("말랑", 채팅_ID);
        좋아요_요청("허브", 채팅_ID);
        좋아요_요청("박스터", 채팅_ID);

        // when
        var 응답 = 채팅에_달린_좋아요_조회_요청(채팅_ID);

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 예상_결과 = 채팅에_달린_좋아요_조회_예상_결과들(
                채팅에_달린_좋아요_조회_예상_결과("박스터", ANDROID),
                채팅에_달린_좋아요_조회_예상_결과("허브", FRONTEND),
                채팅에_달린_좋아요_조회_예상_결과("말랑", BACKEND)
        );
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        좋아요_조회_결과_검증(응답, 예상_결과);
    }

    @Test
    void 내가_좋아요를_누른_채팅을_조회한다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        회원_가입_요청("허브", FRONTEND);
        회원_가입_요청("박스터", ANDROID);
        채팅_생성("박스터", "박스터 질문", "박스터 답변");
        Long 허브_채팅_ID = 채팅_생성("허브", "허브 질문", "허브 답변");
        Long 말랑_채팅_ID = 채팅_생성("말랑", "말랑 질문", "말랑 답변",
                "키워드1", "키워드2", "키워드3");

        좋아요_요청("말랑", 허브_채팅_ID);
        좋아요_요청("말랑", 말랑_채팅_ID);

        // when
        var 응답 = 회원이_좋아요_누른_채팅_조회_요청("말랑");

        // then
        요청_결과의_상태를_검증한다(응답, 정상_요청);
        var 예상_결과 = 내가_좋아요_누른_채팅_조회_결과들(
                내가_좋아요_누른_채팅_조회_결과(
                        말랑_채팅_ID,
                        "말랑",
                        BACKEND,
                        "말랑 질문",
                        1,
                        0,
                        1,
                        조회될_채팅_키워드("키워드1", "키워드2", "키워드3")
                ),
                내가_좋아요_누른_채팅_조회_결과(
                        허브_채팅_ID,
                        "허브",
                        FRONTEND,
                        "허브 질문",
                        1,
                        0,
                        1,
                        조회될_채팅_키워드()
                )
        );
        회원이_좋아요_누른_채팅_조회_결과_검증(응답, 예상_결과);
    }

    @Test
    void 좋아요를_누른_채팅을_조회하면_좋아요를_눌렀는지_알려주는_필드가_참이다() {
        // given
        회원_가입_요청("말랑", BACKEND);
        Long 말랑_채팅_ID = 채팅_생성("말랑", "말랑 질문", "말랑 답변");
        좋아요_요청("말랑", 말랑_채팅_ID);

        // when
        var 응답 = 단일_채팅_조회_요청(말랑_채팅_ID, "말랑");

        // then
        이미_좋아요를_누름(응답);
    }

    @Test
    void 좋아요를_누르지_않은_채팅을_조회하면_좋아요를_눌렀는지_알려주는_필드가_거짓이다() {
        // when
        회원_가입_요청("말랑", BACKEND);
        Long 말랑_채팅_ID = 채팅_생성("말랑", "말랑 질문", "말랑 답변");

        // when
        var 응답 = 단일_채팅_조회_요청(말랑_채팅_ID, "말랑");

        // then
        좋아요를_누르지_않음(응답);
    }
}
