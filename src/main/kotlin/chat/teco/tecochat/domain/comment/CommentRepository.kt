package chat.teco.tecochat.domain.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull

fun CommentRepository.getByIdOrThrow(id: Long) = findByIdOrNull(id)
    ?: throw NoSuchElementException("댓글이 존재하지 않습니다. id: $id")

interface CommentRepository : JpaRepository<Comment, Long> {

    fun findAllByChatId(chatId: Long): List<Comment>
}
