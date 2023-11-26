package chat.teco.tecochat.application

import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.comment.Comment
import chat.teco.tecochat.domain.comment.CommentRepository
import chat.teco.tecochat.domain.member.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class CommentService(
    private val chatRepository: ChatRepository,
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
) {

    fun write(memberId: Long, request: WriteCommentRequest): Long {
        val comment = request.toComment(memberId)
        val chat = chatRepository.getById(request.chatId)
        chat.increaseComment()
        return commentRepository.save(comment).id
    }

    fun update(memberId: Long, commentId: Long, updateCommentRequest: UpdateCommentRequest) {
        val comment = commentRepository.getById(commentId)
        comment.update(memberId, updateCommentRequest.content)
    }

    fun delete(memberId: Long, commentId: Long) {
        val comment = commentRepository.getById(commentId)
        comment.validateDelete(memberId)
        val chat = chatRepository.getById(comment.chatId)
        chat.decreaseComment()
        commentRepository.delete(comment)
    }

    fun findAllByChatId(chatId: Long): List<CommentResponse> {
        val comments = commentRepository.findAllByChatId(chatId)
        val members = memberRepository.findAllById(comments.map(Comment::memberId))
            .associateBy { it.id }
        return comments.map { CommentResponse(it, members[it.memberId]) }
    }
}

