package chat.teco.tecochat.like.chatlike.presentation;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByChatIdQueryDto;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByMemberIdQueryDto;
import chat.teco.tecochat.like.chatlike.application.PushChatLikeService;
import chat.teco.tecochat.like.chatlike.application.PushChatLikeService.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.presentation.request.PushChatLikeRequest;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat-likes")
public class ChatLikeController {

    private final PushChatLikeService pushChatLikeService;
    private final ChatLikeQueryService chatLikeQueryService;

    public ChatLikeController(final PushChatLikeService pushChatLikeService,
                              final ChatLikeQueryService chatLikeQueryService) {
        this.pushChatLikeService = pushChatLikeService;
        this.chatLikeQueryService = chatLikeQueryService;
    }

    @PostMapping
    ResponseEntity<Void> pushLike(
            @Auth final Long memberId,
            @RequestBody final PushChatLikeRequest request
    ) {
        final PushChatLikeCommand command = new PushChatLikeCommand(memberId, request.chatId());
        pushChatLikeService.pushLike(command);
        return ResponseEntity.ok().build();
    }

    @GetMapping(params = "chatId")
    ResponseEntity<List<ChatLikeByChatIdQueryDto>> findAllByChatId(
            @RequestParam(value = "chatId") final Long chatId
    ) {
        return ResponseEntity.ok(chatLikeQueryService.findAllByChatId(chatId));
    }

    @GetMapping
    ResponseEntity<List<ChatLikeByMemberIdQueryDto>> findAllByMemberId(
            @Auth final Long memberId
    ) {
        return ResponseEntity.ok(chatLikeQueryService.findAllByMemberId(memberId));
    }
}
