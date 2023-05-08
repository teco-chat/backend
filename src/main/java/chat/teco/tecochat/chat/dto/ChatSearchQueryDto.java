package chat.teco.tecochat.chat.dto;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import java.time.LocalDateTime;

public record ChatSearchQueryDto(
        Long id,
        Long crewId,
        String crewName,
        Course course,
        String title,
        int likeCount,
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
                chat.likeCount(),
                chat.questionAndAnswers().size(),
                chat.createdAt()
        );
    }
}
