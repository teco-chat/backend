package chat.teco.tecochat.chat.presentation.chat;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.ChatSearchCond;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.common.presentation.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatQueryController {

    private final QueryChatByIdUseCase queryChatByIdUseCase;
    private final SearchChatUseCase searchChatUseCase;

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
