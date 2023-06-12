package chat.teco.tecochat.chat.presentation.chat.api;

import static org.springframework.http.HttpStatus.CREATED;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.chat.application.chat.ChatService;
import chat.teco.tecochat.chat.application.chat.dto.CopyCommand;
import chat.teco.tecochat.chat.application.chat.dto.UpdateChatTitleCommand;
import chat.teco.tecochat.chat.presentation.chat.api.request.UpdateChatTitleRequest;
import chat.teco.tecochat.chat.presentation.chat.api.response.CopyChatResponse;
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
        chatService.updateTitle(
                new UpdateChatTitleCommand(memberId, chatId, request.title())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/copy/{id}")
    ResponseEntity<CopyChatResponse> copy(
            @PathVariable("id") Long chatId,
            @Auth Long memberId
    ) {
        Long copiedId = chatService.copy(
                new CopyCommand(chatId, memberId)
        );
        return ResponseEntity.status(CREATED)
                .body(new CopyChatResponse(copiedId));
    }
}
