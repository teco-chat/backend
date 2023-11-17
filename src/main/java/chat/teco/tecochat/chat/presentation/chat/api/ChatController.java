package chat.teco.tecochat.chat.presentation.chat.api;

import static org.springframework.http.HttpStatus.CREATED;

import chat.teco.tecochat.application.ChatService;
import chat.teco.tecochat.application.UpdateChatTitleRequest;
import chat.teco.tecochat.chat.presentation.chat.api.response.CopyChatResponse;
import chat.teco.tecochat.security.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/chats")
@RestController
public class ChatController {

    private final ChatService chatService;

    @PatchMapping("/{id}")
    ResponseEntity<Void> updateTitle(
            @PathVariable("id") Long chatId,
            @Auth Long memberId,
            @RequestBody UpdateChatTitleRequest request
    ) {
        chatService.updateTitle(memberId, chatId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/copy/{id}")
    ResponseEntity<CopyChatResponse> copy(
            @Auth Long memberId,
            @PathVariable("id") Long chatId
    ) {
        Long copiedId = chatService.copy(memberId, chatId);
        return ResponseEntity.status(CREATED)
                .body(new CopyChatResponse(copiedId));
    }
}
