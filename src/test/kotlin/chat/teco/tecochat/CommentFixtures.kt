package chat.teco.tecochat

import chat.teco.tecochat.application.CommentResponse
import chat.teco.tecochat.application.UpdateCommentRequest
import chat.teco.tecochat.application.WriteCommentRequest
import chat.teco.tecochat.domain.comment.Comment
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.Course.BACKEND
import java.time.LocalDateTime

const val CONTENT = "댓글"
const val UPDATED_CONTENT = "수정된 댓글"
const val CREW_NAME = "herb"

fun createComment(
    chatId: Long = 1L,
    memberId: Long = 1L,
    content: String = CONTENT,
    id: Long = 0L,
): Comment {
    return Comment(chatId, memberId, content, id)
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

fun createCommentResponse(
    id: Long = 0L,
    crewName: String = CREW_NAME,
    course: Course = BACKEND,
    content: String = CONTENT,
    createdAt: LocalDateTime = LocalDateTime.now(),
): CommentResponse {
    return CommentResponse(id, crewName, course, content, createdAt)
}
