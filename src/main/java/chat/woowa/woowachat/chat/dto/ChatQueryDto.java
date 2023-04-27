package chat.woowa.woowachat.chat.dto;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.Message;
import java.time.LocalDateTime;
import java.util.List;

public record ChatQueryDto(
        Long id,
        String crewName,
        String title,
        LocalDateTime createAt,
        List<MessageQueryDto> messages
) {

    public static ChatQueryDto of(final Chat chat, final String crewName) {
        final List<MessageQueryDto> messageQueryDtos = chat.messages()
                .stream()
                .map(MessageQueryDto::from)
                .toList();

        return new ChatQueryDto(
                chat.id(),
                crewName, chat.title(),
                chat.createdAt(),
                messageQueryDtos);
    }

    public record MessageQueryDto(
            Long id,
            String content,
            String role,
            LocalDateTime createAt
    ) {
        public static MessageQueryDto from(final Message message) {
            return new MessageQueryDto(message.id(), message.content(), message.roleName(), message.createdAt());
        }
    }
}
