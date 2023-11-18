package chat.teco.tecochat.application

import chat.teco.tecochat.domain.comment.Comment
import com.fasterxml.jackson.annotation.JsonCreator

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
