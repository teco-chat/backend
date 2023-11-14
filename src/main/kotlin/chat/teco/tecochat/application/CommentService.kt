package chat.teco.tecochat.application

import chat.teco.tecochat.chat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.comment.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CommentService(
    private val chatRepository: ChatRepository,
    private val commentRepository: CommentRepository,
) {

    fun write(memberId: Long, request: WriteCommentRequest): Long {
        val comment = request.toComment(memberId)
        val chat = chatRepository.getById(request.chatId)
        chat.increaseComment()
        return commentRepository.save(comment).id()
    }

    fun update(memberId: Long, commentId: Long, updateCommentRequest: UpdateCommentRequest) {
        val comment = commentRepository.getById(commentId)
        comment.update(memberId, updateCommentRequest.content)
    }

    fun delete(memberId: Long, commentId: Long) {
        val comment = commentRepository.getById(commentId)
        comment.validateDelete(memberId)
        val chat = chatRepository.getById(comment.chatId())
        chat.decreaseComment()
        commentRepository.delete(comment)
    }
}

