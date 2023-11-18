package chat.teco.tecochat.domain.chatlike

import chat.teco.tecochat.support.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class ChatLike(
    @Column(nullable = false)
    var memberId: Long,

    @Column(nullable = false)
    var chatId: Long,

    id: Long = 0L,
) : BaseEntity(id)
