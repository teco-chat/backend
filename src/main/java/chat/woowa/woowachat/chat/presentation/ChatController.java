package chat.woowa.woowachat.chat.presentation;

import static org.springframework.http.HttpStatus.CREATED;

import chat.woowa.woowachat.chat.application.AskChatService;
import chat.woowa.woowachat.chat.application.ChatQueryService;
import chat.woowa.woowachat.chat.dto.AskCommand;
import chat.woowa.woowachat.chat.dto.AskRequest;
import chat.woowa.woowachat.chat.dto.ChatQueryDto;
import chat.woowa.woowachat.chat.dto.MessageDto;
import chat.woowa.woowachat.member.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final AskChatService askChatService;
    private final ChatQueryService chatQueryService;

    public ChatController(final AskChatService askChatService, final ChatQueryService chatQueryService) {
        this.askChatService = askChatService;
        this.chatQueryService = chatQueryService;
    }

    @PostMapping("/chats")
    public ResponseEntity<MessageDto> createAsk(
            @Auth final Long memberId,
            @RequestBody final AskRequest askRequest
    ) {
        final MessageDto answer = askChatService.createAsk(
                new AskCommand(memberId, askRequest.message(), askRequest.token()));
        return ResponseEntity.status(CREATED.value())
                .body(answer);
    }

    @PostMapping("/chats/{id}")
    public ResponseEntity<MessageDto> ask(
            @PathVariable("id") final Long chatId,
            @Auth final Long memberId,
            @RequestBody final AskRequest askRequest
    ) {
        final MessageDto answer = askChatService.ask(
                chatId,
                new AskCommand(memberId, askRequest.message(), askRequest.token()));
        return ResponseEntity.status(CREATED.value())
                .body(answer);
    }

    @GetMapping("/chats/{id}")
    public ResponseEntity<ChatQueryDto> findById(
            @PathVariable final Long id
    ) {
        return ResponseEntity.ok(chatQueryService.findById(id));
    }
}
