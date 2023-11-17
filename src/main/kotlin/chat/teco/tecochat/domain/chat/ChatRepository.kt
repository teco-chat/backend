package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.chat.domain.chat.Chat
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface ChatRepository : JpaRepository<Chat, Long> {
    fun findAllByMemberId(memberId: Long): List<Chat>

    @Query("select c from Chat c left join fetch c.questionAndAnswers.questionAndAnswers qnas where c.id = :id")
    fun findWithQuestionAndAnswersById(@Param("id") id: Long): Optional<Chat>

    fun getWithQuestionAndAnswersById(id: Long): Chat {
        return findWithQuestionAndAnswersById(id)
            .orElseThrow { NoSuchElementException("채팅이 존재하지 않습니다.") }
    }

    override fun getById(id: Long): Chat {
        return findById(id)
            .orElseThrow { NoSuchElementException("채팅이 존재하지 않습니다.") }
    }
}
