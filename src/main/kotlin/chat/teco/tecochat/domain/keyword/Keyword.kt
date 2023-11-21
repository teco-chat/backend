package chat.teco.tecochat.domain.keyword

import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.support.domain.BaseEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Keyword(
    var keyword: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    var chat: Chat,

    id: Long = 0L,
) : BaseEntity(id) {

    constructor(keyword: String, chat: Chat) : this(
        keyword = keyword,
        chat = chat,
        id = 0L
    )

    fun copy(copiedChat: Chat): Keyword {
        return Keyword(keyword, copiedChat)
    }
}
