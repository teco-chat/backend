package chat.teco.tecochat.chat.presentation.chat.api;

import chat.teco.tecochat.chat.query.ChatQueryService;
import chat.teco.tecochat.query.ChatSearchCond;
import chat.teco.tecochat.query.QueryChatByIdResponse;
import chat.teco.tecochat.query.SearchChatResponse;
import chat.teco.tecochat.security.Auth;
import chat.teco.tecochat.support.ui.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/chats")
@RestController
public class ChatQueryController {

    private final ChatQueryService chatQueryService;

    public ChatQueryController(ChatQueryService chatQueryService) {
        this.chatQueryService = chatQueryService;
    }

    @GetMapping("/{id}")
    ResponseEntity<QueryChatByIdResponse> findById(
            @PathVariable Long id,
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(chatQueryService.findById(id, memberId));
    }

    @GetMapping
    ResponseEntity<PageResponse<SearchChatResponse>> search(
            @ModelAttribute ChatSearchCond cond,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
                PageResponse.from(chatQueryService.search(cond, pageable)));
    }
}
