package chat.teco.tecochat.chat.application.chat.usecase;

import chat.teco.tecochat.chat.domain.chat.ChatQueryRepository.ChatSearchCond;
import chat.teco.tecochat.member.domain.Course;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchChatUseCase {

    Page<SearchChatResponse> search(ChatSearchCond cond, Pageable pageable);

    record SearchChatResponse(
            Long id,
            Long crewId,
            String crewName,
            Course course,
            String title,
            int likeCount,
            int totalQnaCount,
            List<SearchKeywordDto> keywords,
            LocalDateTime createdAt
    ) {

        public record SearchKeywordDto(
                String keyword
        ) {
        }
    }
}
