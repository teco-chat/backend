package chat.teco.tecochat

import chat.teco.tecochat.like.chatlike.application.dto.PushChatLikeCommand
import chat.teco.tecochat.like.chatlike.domain.ChatLike

fun createChatLike(
    memberId: Long = 1L,
    chatId: Long = 1L,
    id: Long = 0L
): ChatLike {
    return ChatLike(id, memberId, chatId)
}

fun createChatLikeCommand(
    memberId: Long = 1L,
    chatId: Long = 1L
): PushChatLikeCommand {
    return PushChatLikeCommand(memberId, chatId)
}
