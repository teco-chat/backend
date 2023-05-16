package chat.teco.tecochat.chat.domain.event;

import chat.teco.tecochat.chat.domain.Chat;
import java.time.LocalDateTime;

public record ChatCreatedEvent(
        Long chatId,
        LocalDateTime localDateTime
) {
    public static ChatCreatedEvent from(final Chat chat) {
        return new ChatCreatedEvent(chat.id(), LocalDateTime.now());
    }
}
