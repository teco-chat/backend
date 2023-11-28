package chat.teco.tecochat.application

import chat.teco.tecochat.domain.chat.ChatCopiedEvent
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.getByIdOrThrow
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ChatService(
    private val chatRepository: ChatRepository,
    private val publisher: ApplicationEventPublisher,
) {

    fun updateTitle(memberId: Long, chatId: Long, request: UpdateChatTitleRequest) {
        val chat = chatRepository.getByIdOrThrow(chatId)
        chat.updateTitle(memberId, request.title)
    }

    fun copy(memberId: Long, chatId: Long): Long {
        val chat = chatRepository.getByIdOrThrow(chatId)
        val copiedChat = chatRepository.save(chat.copy(memberId))
        publisher.publishEvent(ChatCopiedEvent(chat.id, copiedChat.id))
        return copiedChat.id
    }
}

