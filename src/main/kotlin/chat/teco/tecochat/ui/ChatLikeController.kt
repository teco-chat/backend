package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatLikeRequest
import chat.teco.tecochat.application.ChatLikeService
import chat.teco.tecochat.security.Auth
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/chat-likes")
class ChatLikeController(
    private val chatLikeService: ChatLikeService
) {

    @PostMapping
    fun pushLike(
        @Auth memberId: Long,
        @RequestBody request: ChatLikeRequest
    ): ResponseEntity<Void> {
        chatLikeService.pushLike(memberId, request.chatId)
        return ResponseEntity.ok().build()
    }
}

