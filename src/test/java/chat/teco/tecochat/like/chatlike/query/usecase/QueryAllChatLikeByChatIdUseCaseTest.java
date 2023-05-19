package chat.teco.tecochat.like.chatlike.query.usecase;

import static chat.teco.tecochat.member.domain.Course.ANDROID;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.like.chatlike.query.ChatLikeQueryUseCaseTest;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse.MemberInfo;
import chat.teco.tecochat.member.domain.Course;
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
    private QueryAllChatLikeByChatIdUseCase queryAllChatLikeByChatIdUseCase;

    @Test
    void 채팅에_달린_좋아요를_조회한다() {
        // when
        List<QueryChatLikeByChatIdResponse> chatIdQueryDtos =
                queryAllChatLikeByChatIdUseCase.findAllByChatId(말랑_채팅.id());

        // then
        assertThat(chatIdQueryDtos).hasSize(3);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(0),
                박스터_말랑채팅_좋아요.id(), 박스터.id(), "박스터", ANDROID);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(1),
                허브_말랑채팅_좋아요.id(), 허브.id(), "허브_좋아요", FRONTEND);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(2),
                말랑_말랑채팅_좋아요.id(), 말랑.id(), "말랑_좋아요", BACKEND);
    }

    private void 채팅으로_조회한_좋아요의_정보를_검증한다(
            QueryChatLikeByChatIdResponse chatIdQueryDto,
            Long chatLikeId,
            Long memberId,
            String memberName,
            Course course
    ) {
        assertThat(chatIdQueryDto.id()).isEqualTo(chatLikeId);
        MemberInfo memberInfo = chatIdQueryDto.memberInfo();
        assertThat(memberInfo.id()).isEqualTo(memberId);
        assertThat(memberInfo.crewName()).isEqualTo(memberName);
        assertThat(memberInfo.course()).isEqualTo(course);
    }
}
