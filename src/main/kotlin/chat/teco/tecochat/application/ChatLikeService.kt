package chat.teco.tecochat.application

import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chatlike.ChatLike
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import chat.teco.tecochat.query.ChatLikeQueryRepository
import chat.teco.tecochat.query.QueryChatLikeByChatIdResponse
import chat.teco.tecochat.query.QueryChatLikedByMemberIdResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Transactional
@Service
class ChatLikeService(
    private val chatLikeRepository: ChatLikeRepository,
    private val chatLikeQueryRepository: ChatLikeQueryRepository,
    private val chatRepository: ChatRepository,
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

    fun findAllByChatId(chatId: Long): List<QueryChatLikeByChatIdResponse> =
        chatLikeQueryRepository.findAllByChatId(chatId)

    fun findAllByMemberId(chatId: Long, pageable: Pageable): Page<QueryChatLikedByMemberIdResponse> =
        chatLikeQueryRepository.findAllByMemberId(chatId, pageable)
}
