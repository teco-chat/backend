package chat.teco.tecochat.like.chatlike.presentation;

import chat.teco.tecochat.common.presentation.PageResponse;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikedByMemberIdResponse;
import chat.teco.tecochat.security.Auth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat-likes")
public class ChatLikeQueryController {

    private final QueryAllChatLikeByChatIdUseCase queryAllByChatIdUseCase;
    private final QueryAllChatLikedByMemberIdUseCase queryAllByMemberIdUseCase;

    @GetMapping(params = "chatId")
    ResponseEntity<List<QueryChatLikeByChatIdResponse>> findAllByChatId(
            @RequestParam(value = "chatId") Long chatId
    ) {
        return ResponseEntity.ok(queryAllByChatIdUseCase.findAllByChatId(chatId));
    }

    @GetMapping
    ResponseEntity<PageResponse<QueryChatLikedByMemberIdResponse>> findAllByMemberId(
            @Auth Long memberId,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.from(queryAllByMemberIdUseCase.findAllByMemberId(memberId, pageable)));
    }
}
