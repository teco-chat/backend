package chat.teco.tecochat.like.chatlike.query.usecase;

import chat.teco.tecochat.member.domain.Course;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryAllChatLikedByMemberIdUseCase {

    Page<QueryChatLikeByMemberIdResponse> findAllByMemberId(Long memberId, Pageable pageable);

    /**
     * 회원이 누른 좋아요를 조회할때 반환되는 정보
     */
    record QueryChatLikeByMemberIdResponse(
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
