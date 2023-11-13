package chat.teco.tecochat.application

import chat.teco.tecochat.chat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import chat.teco.tecochat.like.chatlike.application.dto.PushChatLikeCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional
@Service
class ChatLikeService(
    private val chatLikeRepository: ChatLikeRepository,
    private val chatRepository: ChatRepository
) {

    fun pushLike(command: PushChatLikeCommand) {
        val chat = chatRepository.getById(command.chatId)
        chatLikeRepository.findByMemberIdAndChatId(command.memberId, command.chatId)?.let {
            chatLikeRepository.delete(it)
            chat.decreaseLike()
            return
        }
        chatLikeRepository.save(command.toChatLike())
        chat.increaseLike()
    }
}
