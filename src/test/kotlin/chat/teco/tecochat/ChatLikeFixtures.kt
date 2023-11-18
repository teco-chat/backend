package chat.teco.tecochat

import chat.teco.tecochat.application.ChatLikeRequest
import chat.teco.tecochat.domain.chatlike.ChatLike

fun createChatLike(
    memberId: Long = 1L,
    chatId: Long = 1L,
    id: Long = 0L,
): ChatLike {
    return ChatLike(id, memberId, chatId)
}

fun createChatLikeRequest(
    chatId: Long = 1L,
): ChatLikeRequest {
    return ChatLikeRequest(chatId)
}
