package chat.teco.tecochat.like.chatlike.application.usecase;

import static chat.teco.tecochat.member.domain.Course.ANDROID;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryUseCaseTest;
import chat.teco.tecochat.like.chatlike.application.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikeByMemberIdResponse;
import chat.teco.tecochat.like.chatlike.application.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikeByMemberIdResponse.ChatInfo;
import chat.teco.tecochat.member.domain.Course;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("QueryAllChatLikedByMemberIdUseCase(회원이 작성한 모든 댓글 조회) 은(는)")
class QueryAllChatLikedByMemberIdUseCaseTest extends ChatLikeQueryUseCaseTest {

    @Autowired
    private QueryAllChatLikedByMemberIdUseCase queryAllChatLikedByMemberIdUseCase;

    @Test
    void 회원이_누른_좋아요를_조회한다() {
        // when
        List<QueryChatLikeByMemberIdResponse> allByMemberId =
                queryAllChatLikedByMemberIdUseCase.findAllByMemberId(허브.id());

        // then
        회원으로_조회한_좋아요의_정보를_검증한다(allByMemberId.get(0),
                허브_박스터채팅_좋아요.id(),
                박스터_채팅.id(),
                박스터_채팅.memberId(),
                "박스터",
                ANDROID,
                박스터_채팅.title(),
                3,
                0);

        회원으로_조회한_좋아요의_정보를_검증한다(allByMemberId.get(1),
                허브_허브채팅_좋아요.id(),
                허브_채팅.id(),
                허브_채팅.memberId(),
                "허브_좋아요",
                FRONTEND,
                허브_채팅.title(),
                3,
                0);

        회원으로_조회한_좋아요의_정보를_검증한다(allByMemberId.get(2),
                허브_말랑채팅_좋아요.id(),
                말랑_채팅.id(),
                말랑_채팅.memberId(),
                "말랑_좋아요",
                BACKEND,
                말랑_채팅.title(),
                3,
                0);
    }

    private void 회원으로_조회한_좋아요의_정보를_검증한다(
            QueryChatLikeByMemberIdResponse memberIdQueryDto,
            Long chatLikeId,
            Long chatId,
            Long crewId,
            String crewName,
            Course course,
            String title,
            int likeCount,
            int totalQnaCount
    ) {
        assertThat(memberIdQueryDto.id()).isEqualTo(chatLikeId);
        ChatInfo chatInfo = memberIdQueryDto.chatInfo();
        assertThat(chatInfo.id()).isEqualTo(chatId);
        assertThat(chatInfo.crewId()).isEqualTo(crewId);
        assertThat(chatInfo.crewName()).isEqualTo(crewName);
        assertThat(chatInfo.course()).isEqualTo(course);
        assertThat(chatInfo.title()).isEqualTo(title);
        assertThat(chatInfo.likeCount()).isEqualTo(likeCount);
        assertThat(chatInfo.totalQnaCount()).isEqualTo(totalQnaCount);
    }
}
