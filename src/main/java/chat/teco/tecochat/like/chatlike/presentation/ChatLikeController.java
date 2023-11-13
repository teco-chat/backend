package chat.teco.tecochat.like.chatlike.presentation;

import chat.teco.tecochat.application.ChatLikeService;
import chat.teco.tecochat.like.chatlike.application.dto.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.presentation.request.PushChatLikeRequest;
import chat.teco.tecochat.security.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat-likes")
public class ChatLikeController {

    private final ChatLikeService chatLikeService;

    @PostMapping
    ResponseEntity<Void> pushLike(
            @Auth Long memberId,
            @RequestBody PushChatLikeRequest request
    ) {
        chatLikeService.pushLike(new PushChatLikeCommand(memberId, request.chatId()));
        return ResponseEntity.ok().build();
    }
}
