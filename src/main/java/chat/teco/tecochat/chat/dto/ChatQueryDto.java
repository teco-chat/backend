package chat.teco.tecochat.chat.dto;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.Message;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.member.domain.Course;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public record ChatQueryDto(
        Long id,
        String crewName,
        Course course,
        String title,
        LocalDateTime createdAt,
        List<MessageQueryDto> messages
) {

    public static ChatQueryDto of(final Chat chat, final String crewName, final Course course) {
        final List<MessageQueryDto> messages = new ArrayList<>();
        for (final QuestionAndAnswer qna : chat.questionAndAnswers()) {
            messages.add(MessageQueryDto.of(qna.question(), qna.createdAt()));
            messages.add(MessageQueryDto.of(qna.answer(), qna.createdAt()));
        }
        return new ChatQueryDto(
                chat.id(),
                crewName,
                course,
                chat.title(),
                chat.createdAt(),
                messages);
    }

    // TODO 반환형식 바꿀 수 있으면 QnaDto 만들어서 바꾸기
    public record MessageQueryDto(
            String content,
            String role,
            LocalDateTime createdAt
    ) {
        public static MessageQueryDto of(final Message message,
                                         final LocalDateTime createdAt) {
            return new MessageQueryDto(
                    message.content(),
                    message.roleName(),
                    createdAt
            );
        }
    }
}
