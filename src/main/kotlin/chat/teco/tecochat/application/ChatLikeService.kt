package chat.teco.tecochat.application

import chat.teco.tecochat.chat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import chat.teco.tecochat.like.chatlike.domain.ChatLike
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional
@Service
class ChatLikeService(
    private val chatLikeRepository: ChatLikeRepository,
    private val chatRepository: ChatRepository
) {

    fun pushLike(memberId: Long, chatId: Long) {
        val chat = chatRepository.getById(chatId)
        chatLikeRepository.findByMemberIdAndChatId(memberId, chatId)?.let {
            chatLikeRepository.delete(it)
            chat.decreaseLike()
            return
        }
        chatLikeRepository.save(ChatLike(memberId, chatId))
        chat.increaseLike()
    }
}
