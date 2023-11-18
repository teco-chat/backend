package chat.teco.tecochat.domain.chatlike

import org.springframework.data.jpa.repository.JpaRepository

interface ChatLikeRepository : JpaRepository<ChatLike, Long> {

    fun findByMemberIdAndChatId(memberId: Long, chatId: Long): ChatLike?
}
