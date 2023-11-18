package chat.teco.tecochat.like.chatlike.query.usecase;

import chat.teco.tecochat.domain.member.Course;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QueryAllChatLikedByMemberIdUseCase {

    Page<QueryChatLikedByMemberIdResponse> findAllByMemberId(Long memberId, Pageable pageable);

    /**
     * 회원이 누른 좋아요를 조회할때 반환되는 정보
     */
    record QueryChatLikedByMemberIdResponse(
            Long id,  // 채팅 ID
            Long crewId,
            String crewName,
            Course course,
            String title,
            int likeCount,
            int commentCount,
            int totalQnaCount,
            List<QueryLikedChatKeywordDto> keywords,
            LocalDateTime createdAt
    ) {
        public QueryChatLikedByMemberIdResponse(
                Long id,
                Long crewId, String crewName, Course course,
                String title, int likeCount, int commentCount,
                int totalQnaCount, LocalDateTime createdAt
        ) {
            this(id, crewId, crewName, course, title, likeCount, commentCount, totalQnaCount, new ArrayList<>(),
                    createdAt);
        }

        public void addKeywords(List<QueryLikedChatKeywordDto> queryLikedChatKeywordDtos) {
            this.keywords.addAll(queryLikedChatKeywordDtos);
        }

        public record QueryLikedChatKeywordDto(
                String keyword
        ) {
        }
    }
}
