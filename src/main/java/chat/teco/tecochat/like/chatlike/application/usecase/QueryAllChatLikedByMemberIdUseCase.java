package chat.teco.tecochat.like.chatlike.application.usecase;

import chat.teco.tecochat.member.domain.Course;
import java.time.LocalDateTime;
import java.util.List;

public interface QueryAllChatLikedByMemberIdUseCase {

    List<QueryChatLikeByMemberIdResponse> findAllByMemberId(Long memberId);

    /**
     * 회원이 누른 좋아요를 조회할때 반환되는 정보
     */
    record QueryChatLikeByMemberIdResponse(
            Long id,
            LocalDateTime createdAt,
            ChatInfo chatInfo
    ) {
        public record ChatInfo(
                Long id,
                Long crewId,
                String crewName,
                Course course,
                String title,
                int likeCount,
                int totalQnaCount,
                LocalDateTime createdAt
        ) {
        }
    }
}
