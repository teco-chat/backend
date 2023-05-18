package chat.teco.tecochat.like.chatlike.presentation;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.presentation.request.PushChatLikeRequest;
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

    private final PushChatLikeUseCase pushUseCase;

    @PostMapping
    ResponseEntity<Void> pushLike(
            @Auth Long memberId,
            @RequestBody PushChatLikeRequest request
    ) {
        pushUseCase.pushLike(new PushChatLikeCommand(memberId, request.chatId()));
        return ResponseEntity.ok().build();
    }
}
