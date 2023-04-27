package chat.woowa.woowachat.chat.dto;

import chat.woowa.woowachat.member.domain.Course;
import java.time.LocalDateTime;

public record ChatSearchQueryDto(
        Long id,
        Long crewId,
        String crewName,
        Course course,
        String title,
        LocalDateTime createdAt
) {
}
