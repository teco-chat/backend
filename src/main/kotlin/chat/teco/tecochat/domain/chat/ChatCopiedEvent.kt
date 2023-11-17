package chat.teco.tecochat.domain.chat

data class ChatCopiedEvent(
    val originChatId: Long,
    val copiedChatId: Long,
)

