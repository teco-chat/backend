package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.support.domain.BaseEventHistory
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("ChatCreatedEventHistory")
class ChatCreatedEventHistory(
    override var eventDateTime: LocalDateTime,
    var chatId: Long,
) : BaseEventHistory(eventDateTime)
