package chat.teco.tecochat.like.chatlike.presentation;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.application.usecase.QueryAllChatLikeByChatIdUseCase;
import chat.teco.tecochat.like.chatlike.application.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse;
import chat.teco.tecochat.like.chatlike.application.usecase.QueryAllChatLikedByMemberIdUseCase;
import chat.teco.tecochat.like.chatlike.application.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikeByMemberIdResponse;
import chat.teco.tecochat.like.chatlike.presentation.request.PushChatLikeRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat-likes")
public class ChatLikeController {

    private final PushChatLikeUseCase pushUseCase;
    private final QueryAllChatLikeByChatIdUseCase queryAllByChatIdUseCase;
    private final QueryAllChatLikedByMemberIdUseCase queryAllByMemberIdUseCase;

    @PostMapping
    ResponseEntity<Void> pushLike(
            @Auth Long memberId,
            @RequestBody PushChatLikeRequest request
    ) {
        pushUseCase.pushLike(new PushChatLikeCommand(memberId, request.chatId()));
        return ResponseEntity.ok().build();
    }

    @GetMapping(params = "chatId")
    ResponseEntity<List<QueryChatLikeByChatIdResponse>> findAllByChatId(
            @RequestParam(value = "chatId") Long chatId
    ) {
        return ResponseEntity.ok(queryAllByChatIdUseCase.findAllByChatId(chatId));
    }

    @GetMapping
    ResponseEntity<List<QueryChatLikeByMemberIdResponse>> findAllByMemberId(
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(queryAllByMemberIdUseCase.findAllByMemberId(memberId));
    }
}
