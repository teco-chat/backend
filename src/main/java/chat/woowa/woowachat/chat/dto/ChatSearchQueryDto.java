package chat.woowa.woowachat.chat.dto;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.Member;
import java.time.LocalDateTime;

public record ChatSearchQueryDto(
        Long id,
        Long crewId,
        String crewName,
        Course course,
        String title,
        int totalQnaCount,
        LocalDateTime createdAt
) {
    public static ChatSearchQueryDto from(final Chat chat, final Member member) {
        return new ChatSearchQueryDto(
                chat.id(),
                member.id(),
                member.name(),
                member.course(),
                chat.title(),
                chat.questionAndAnswers().size(),
                chat.createdAt()
        );
    }
}
