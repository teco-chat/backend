package chat.teco.tecochat

import chat.teco.tecochat.application.UpdateCommentRequest
import chat.teco.tecochat.application.WriteCommentRequest
import chat.teco.tecochat.comment.domain.Comment

const val CONTENT = "댓글"
const val UPDATED_CONTENT = "수정된 댓글"

fun createComment(
    chatId: Long = 1L,
    memberId: Long = 1L,
    content: String = CONTENT,
    id: Long = 0L,
): Comment {
    return Comment(id, chatId, memberId, content)
}

fun createWriteCommentRequest(
    chatId: Long = 1L,
    content: String = CONTENT,
): WriteCommentRequest {
    return WriteCommentRequest(chatId, content)
}

fun createUpdateCommentRequest(
    content: String = UPDATED_CONTENT,
): UpdateCommentRequest {
    return UpdateCommentRequest(content)
}
