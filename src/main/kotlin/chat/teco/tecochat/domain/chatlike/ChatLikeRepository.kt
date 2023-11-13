package chat.teco.tecochat.domain.chatlike

import chat.teco.tecochat.like.chatlike.domain.ChatLike
import org.springframework.data.jpa.repository.JpaRepository

interface ChatLikeRepository : JpaRepository<ChatLike, Long> {

    fun findByMemberIdAndChatId(memberId: Long, chatId: Long): ChatLike?
}
