package chat.teco.tecochat.ui

import chat.teco.tecochat.application.CommentResponse
import chat.teco.tecochat.application.CommentService
import chat.teco.tecochat.application.UpdateCommentRequest
import chat.teco.tecochat.application.WriteCommentRequest
import chat.teco.tecochat.application.WriteCommentResponse
import chat.teco.tecochat.security.Auth
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/comments")
class CommentController(
    private val commentService: CommentService,
) {

    @PostMapping
    fun write(
        @Auth memberId: Long,
        @RequestBody request: WriteCommentRequest,
    ): ResponseEntity<WriteCommentResponse> {
        val id = commentService.write(memberId, request)
        val uri = URI(id.toString())
        return ResponseEntity.created(uri).body(WriteCommentResponse(id))
    }

    @PatchMapping("/{id}")
    fun update(
        @Auth memberId: Long,
        @PathVariable("id") commentId: Long,
        @RequestBody request: UpdateCommentRequest,
    ): ResponseEntity<Void> {
        commentService.update(memberId, commentId, request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun delete(
        @Auth memberId: Long,
        @PathVariable("id") commentId: Long,
    ): ResponseEntity<Void> {
        commentService.delete(memberId, commentId)
        return ResponseEntity.ok().build()
    }

    @GetMapping
    fun findAllByChatId(
        @RequestParam("chatId") chatId: Long,
    ): ResponseEntity<List<CommentResponse>> {
        return ResponseEntity.ok(commentService.findAllByChatId(chatId))
    }
}

