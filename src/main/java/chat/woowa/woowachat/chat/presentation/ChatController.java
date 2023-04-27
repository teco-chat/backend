package chat.woowa.woowachat.chat.presentation;

import static org.springframework.http.HttpStatus.CREATED;

import chat.woowa.woowachat.chat.application.AskChatService;
import chat.woowa.woowachat.chat.dto.AskCommand;
import chat.woowa.woowachat.chat.dto.AskRequest;
import chat.woowa.woowachat.chat.dto.MessageDto;
import chat.woowa.woowachat.member.presentation.argumentresolver.Auth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final AskChatService askChatService;

    public ChatController(final AskChatService askChatService) {
        this.askChatService = askChatService;
    }

    @PostMapping("/chats")
    public ResponseEntity<MessageDto> ask(
            @Auth final Long memberId,
            @RequestBody final AskRequest askRequest
    ) {
        final MessageDto answer = askChatService.ask(
                new AskCommand(memberId, askRequest.message(), askRequest.token()));
        return ResponseEntity.status(CREATED.value())
                .body(answer);
    }
}
