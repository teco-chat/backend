package chat.teco.tecochat.domain.keyword

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface KeywordRepository : JpaRepository<Keyword, Long> {

    fun findAllByChatId(chatId: Long): List<Keyword>

    @Query("select k from Keyword k where k.chat.id in (:chatIds)")
    fun findAllInChatIds(@Param("chatIds") chatIds: List<Long>): List<Keyword>
}
