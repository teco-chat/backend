package chat.teco.tecochat.application

import chat.teco.tecochat.domain.comment.Comment
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.Member
import com.fasterxml.jackson.annotation.JsonCreator
import java.time.LocalDateTime

data class WriteCommentRequest(
    val chatId: Long,
    val content: String,
) {
    fun toComment(memberId: Long): Comment {
        return Comment(chatId, memberId, content)
    }
}

data class UpdateCommentRequest @JsonCreator constructor(
    val content: String,
)

data class WriteCommentResponse(
    val id: Long,
)

data class CommentQueryResponse(
    val id: Long,
    val crewName: String?,
    val course: Course?,
    val content: String,
    val createdAt: LocalDateTime,
) {
    constructor(comment: Comment, member: Member?) : this(
        comment.id,
        member?.name,
        member?.course,
        comment.content,
        comment.createdAt
    )
}
