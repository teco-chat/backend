package chat.teco.tecochat.chat.query.usecase;

import chat.teco.tecochat.domain.member.Course;
import java.time.LocalDateTime;
import java.util.List;

public interface QueryChatByIdUseCase {

    QueryChatByIdResponse findById(Long id, Long requesterMemberId);

    record QueryChatByIdResponse(
            Long id,
            String crewName,
            Course course,
            String title,
            int likeCount,
            boolean isAlreadyClickLike,
            LocalDateTime createdAt,
            List<QueryMessageDto> messages,
            List<QueryKeywordDto> keywords
    ) {
        public record QueryMessageDto(
                String content,
                String role,
                LocalDateTime createdAt
        ) {
        }

        public record QueryKeywordDto(
                String keyword
        ) {
        }
    }
}
