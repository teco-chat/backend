package chat.teco.tecochat.chat.domain.chat.event;

import chat.teco.tecochat.common.event.BaseEventHistory;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("ChatCreatedEventHistory")
public class ChatCreatedEventHistory extends BaseEventHistory {

    private Long chatId;

    protected ChatCreatedEventHistory() {
    }

    public ChatCreatedEventHistory(
            LocalDateTime eventDateTime,
            Long chatId
    ) {
        super(eventDateTime);
        this.chatId = chatId;
    }

    public Long chatId() {
        return chatId;
    }
}
