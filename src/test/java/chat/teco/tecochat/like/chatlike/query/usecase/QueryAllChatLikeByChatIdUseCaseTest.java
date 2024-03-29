package chat.teco.tecochat.like.chatlike.query.usecase;

import static chat.teco.tecochat.domain.member.Course.ANDROID;
import static chat.teco.tecochat.domain.member.Course.BACKEND;
import static chat.teco.tecochat.domain.member.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.like.chatlike.query.ChatLikeQueryUseCaseTest;
import chat.teco.tecochat.query.ChatLikeQueryRepository;
import chat.teco.tecochat.query.MemberInfo;
import chat.teco.tecochat.query.QueryChatLikeByChatIdResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("QueryAllChatLikeByChatIdUseCase(채팅에 달린 모든 좋아요 조회) 은(는)")
class QueryAllChatLikeByChatIdUseCaseTest extends ChatLikeQueryUseCaseTest {

    @Autowired
    private ChatLikeQueryRepository chatLikeQueryRepository;

    @Test
    void 채팅에_달린_좋아요를_조회한다() {
        // when
        List<QueryChatLikeByChatIdResponse> chatIdQueryDtos =
                chatLikeQueryRepository.findAllByChatId(말랑_채팅.getId());

        // then
        assertThat(chatIdQueryDtos).hasSize(3);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(0),
                박스터_말랑채팅_좋아요.getId(), 박스터.getId(), "박스터", ANDROID);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(1),
                허브_말랑채팅_좋아요.getId(), 허브.getId(), "허브_좋아요", FRONTEND);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(2),
                말랑_말랑채팅_좋아요.getId(), 말랑.getId(), "말랑_좋아요", BACKEND);
    }

    private void 채팅으로_조회한_좋아요의_정보를_검증한다(
            QueryChatLikeByChatIdResponse chatIdQueryDto,
            Long chatLikeId,
            Long memberId,
            String memberName,
            Course course
    ) {
        assertThat(chatIdQueryDto.getId()).isEqualTo(chatLikeId);
        MemberInfo memberInfo = chatIdQueryDto.getMemberInfo();
        assertThat(memberInfo.getId()).isEqualTo(memberId);
        assertThat(memberInfo.getCrewName()).isEqualTo(memberName);
        assertThat(memberInfo.getCourse()).isEqualTo(course);
    }
}
