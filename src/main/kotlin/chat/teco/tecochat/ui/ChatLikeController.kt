package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatLikeRequest
import chat.teco.tecochat.application.ChatLikeService
import chat.teco.tecochat.domain.chatlike.QueryChatLikeByChatIdResponse
import chat.teco.tecochat.domain.chatlike.QueryChatLikedByMemberIdResponse
import chat.teco.tecochat.security.Auth
import chat.teco.tecochat.support.ui.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/chat-likes")
class ChatLikeController(
    private val chatLikeService: ChatLikeService,
) {

    @PostMapping
    fun pushLike(
        @Auth memberId: Long,
        @RequestBody request: ChatLikeRequest,
    ): ResponseEntity<Void> {
        chatLikeService.pushLike(memberId, request.chatId)
        return ResponseEntity.ok().build()
    }

    @GetMapping(params = ["chatId"])
    fun findAllByChatId(
        @RequestParam(value = "chatId") chatId: Long,
    ): ResponseEntity<List<QueryChatLikeByChatIdResponse>> {
        return ResponseEntity.ok(chatLikeService.findAllByChatId(chatId))
    }

    @GetMapping
    fun findAllByMemberId(
        @Auth memberId: Long,
        @PageableDefault(size = 20) pageable: Pageable,
    ): ResponseEntity<PageResponse<QueryChatLikedByMemberIdResponse>> {
        return ResponseEntity.ok(
            PageResponse.from(chatLikeService.findAllByMemberId(memberId, pageable))
        )
    }
}

