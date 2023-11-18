package chat.teco.tecochat.chat.presentation.chat.api;

import chat.teco.tecochat.chat.query.dao.ChatQueryDao.ChatSearchCond;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.common.presentation.PageResponse;
import chat.teco.tecochat.security.Auth;
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

    private final QueryChatByIdUseCase queryChatByIdUseCase;
    private final SearchChatUseCase searchChatUseCase;

    public ChatQueryController(QueryChatByIdUseCase queryChatByIdUseCase, SearchChatUseCase searchChatUseCase) {
        this.queryChatByIdUseCase = queryChatByIdUseCase;
        this.searchChatUseCase = searchChatUseCase;
    }

    @GetMapping("/{id}")
    ResponseEntity<QueryChatByIdResponse> findById(
            @PathVariable Long id,
            @Auth Long memberId
    ) {
        return ResponseEntity.ok(queryChatByIdUseCase.findById(id, memberId));
    }

    @GetMapping
    ResponseEntity<PageResponse<SearchChatResponse>> search(
            @ModelAttribute ChatSearchCond cond,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(
                PageResponse.from(searchChatUseCase.search(cond, pageable)));
    }
}
