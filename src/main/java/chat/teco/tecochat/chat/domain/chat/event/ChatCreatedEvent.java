package chat.teco.tecochat.chat.domain.chat.event;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.common.event.BaseEvent;
import chat.teco.tecochat.common.event.BaseEventHistory;
import java.time.LocalDateTime;

public class ChatCreatedEvent extends BaseEvent {

    private final Long chatId;
    private final LocalDateTime localDateTime;

    public ChatCreatedEvent(
            Long chatId,
            LocalDateTime localDateTime
    ) {
        this.chatId = chatId;
        this.localDateTime = localDateTime;
    }

    public static ChatCreatedEvent from(Chat chat) {
        return new ChatCreatedEvent(chat.id(), LocalDateTime.now());
    }

    public Long chatId() {
        return chatId;
    }

    public LocalDateTime localDateTime() {
        return localDateTime;
    }

    @Override
    public BaseEventHistory history() {
        return new ChatCreatedEventHistory(localDateTime, chatId);
    }
}
