package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.support.domain.BaseEvent
import chat.teco.tecochat.support.domain.BaseEventHistory
import java.time.LocalDateTime

class ChatCreatedEvent(
    val chatId: Long,
    val localDateTime: LocalDateTime,
) : BaseEvent() {

    override fun history(): BaseEventHistory {
        return ChatCreatedEventHistory(localDateTime, chatId)
    }

    override fun processedHistory(): BaseEventHistory {
        return ChatCreatedEventHistory(localDateTime, chatId).apply {
            process()
        }
    }

    companion object {
        @JvmStatic
        fun from(chat: Chat): ChatCreatedEvent {
            return ChatCreatedEvent(chat.id, LocalDateTime.now())
        }
    }
}
