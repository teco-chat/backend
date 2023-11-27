package chat.teco.tecochat.like.chatlike.query.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.like.chatlike.query.ChatLikeQueryUseCaseTest;
import chat.teco.tecochat.query.ChatLikeQueryRepository;
import chat.teco.tecochat.query.QueryChatLikedByMemberIdResponse;
import chat.teco.tecochat.query.QueryLikedChatKeywordDto;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("QueryAllChatLikedByMemberIdUseCase(회원이 좋아요 누른 모든 게시물 조회) 은(는)")
class QueryAllChatLikedByMemberIdUseCaseTest extends ChatLikeQueryUseCaseTest {

    @Autowired
    private ChatLikeQueryRepository chatLikeQueryRepository;

    @Test
    void 회원이_좋아요_누른_게시물을_조회한다() {
        // when
        Page<QueryChatLikedByMemberIdResponse> allByMemberId =
                chatLikeQueryRepository.findAllByMemberId(허브.getId(), PageRequest.of(0, 20));

        // then
        회원이_좋아요_누른_게시물의_정보를_검증한다(
                allByMemberId.getContent().get(0),
                박스터_채팅.getId(),
                박스터_채팅.getMemberId(),
                "박스터",
                Course.ANDROID,
                박스터_채팅.getTitle(),
                3,
                0,
                0,
                키워드들());

        회원이_좋아요_누른_게시물의_정보를_검증한다(
                allByMemberId.getContent().get(1),
                허브_채팅.getId(),
                허브_채팅.getMemberId(),
                "허브_좋아요",
                Course.FRONTEND,
                허브_채팅.getTitle(),
                3,
                0,
                0,
                키워드들(허브채팅_키워드1,
                        허브채팅_키워드2,
                        허브채팅_키워드3));

        회원이_좋아요_누른_게시물의_정보를_검증한다(
                allByMemberId.getContent().get(2),
                말랑_채팅.getId(),
                말랑_채팅.getMemberId(),
                "말랑_좋아요",
                Course.BACKEND,
                말랑_채팅.getTitle(),
                3,
                0,
                0,
                키워드들(말랑채팅_키워드1,
                        말랑채팅_키워드2,
                        말랑채팅_키워드3));
    }

    private List<QueryLikedChatKeywordDto> 키워드들(String... keywords) {
        return Arrays.stream(keywords)
                .map(QueryLikedChatKeywordDto::new)
                .toList();
    }

    private void 회원이_좋아요_누른_게시물의_정보를_검증한다(
            QueryChatLikedByMemberIdResponse 실제_결과,
            Long 채팅_ID,
            Long 회원_ID,
            String 회원이름,
            Course 과정,
            String 제목,
            int 좋아요_수,
            int 댓글_수,
            int 전체_질문답변_수,
            List<QueryLikedChatKeywordDto> 키워드들
    ) {
        assertThat(실제_결과.getId()).isEqualTo(채팅_ID);
        assertThat(실제_결과.getCrewId()).isEqualTo(회원_ID);
        assertThat(실제_결과.getCrewName()).isEqualTo(회원이름);
        assertThat(실제_결과.getCourse()).isEqualTo(과정);
        assertThat(실제_결과.getTitle()).isEqualTo(제목);
        assertThat(실제_결과.getLikeCount()).isEqualTo(좋아요_수);
        assertThat(실제_결과.getCommentCount()).isEqualTo(댓글_수);
        assertThat(실제_결과.getTotalQnaCount()).isEqualTo(전체_질문답변_수);
        assertThat(실제_결과.getKeywords()).usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(키워드들);
    }
}
