package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatService
import chat.teco.tecochat.application.CopyChatResponse
import chat.teco.tecochat.application.UpdateChatTitleRequest
import chat.teco.tecochat.security.Auth
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RequestMapping("/chats")
@RestController
class ChatController(
    private val chatService: ChatService,
) {

    @PatchMapping("/{id}")
    fun updateTitle(
        @PathVariable("id") chatId: Long,
        @Auth memberId: Long,
        @RequestBody request: UpdateChatTitleRequest,
    ): ResponseEntity<Void> {
        chatService.updateTitle(memberId, chatId, request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/copy/{id}")
    fun copy(
        @Auth memberId: Long,
        @PathVariable("id") chatId: Long,
    ): ResponseEntity<CopyChatResponse> {
        val copiedId = chatService.copy(memberId, chatId)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(CopyChatResponse(copiedId))
    }
}
