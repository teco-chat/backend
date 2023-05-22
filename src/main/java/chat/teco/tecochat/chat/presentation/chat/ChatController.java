package chat.teco.tecochat.chat.presentation.chat;

import static org.springframework.http.HttpStatus.CREATED;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.chat.application.chat.usecase.AskUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.AskUseCase.AskCommand;
import chat.teco.tecochat.chat.application.chat.usecase.AskUseCase.AskResult;
import chat.teco.tecochat.chat.application.chat.usecase.CopyChatUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.CopyChatUseCase.CopyCommand;
import chat.teco.tecochat.chat.application.chat.usecase.CreateChatUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.CreateChatUseCase.CreateChatCommand;
import chat.teco.tecochat.chat.application.chat.usecase.CreateChatUseCase.CreateChatResult;
import chat.teco.tecochat.chat.application.chat.usecase.UpdateChatTitleUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.UpdateChatTitleUseCase.UpdateChatTitleCommand;
import chat.teco.tecochat.chat.presentation.chat.request.AskRequest;
import chat.teco.tecochat.chat.presentation.chat.request.CreateChatRequest;
import chat.teco.tecochat.chat.presentation.chat.request.UpdateChatTitleRequest;
import chat.teco.tecochat.chat.presentation.chat.response.AskResponse;
import chat.teco.tecochat.chat.presentation.chat.response.CopyChatResponse;
import chat.teco.tecochat.chat.presentation.chat.response.CreateChatResponse;
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

    private final CreateChatUseCase createChatUseCase;
    private final AskUseCase askUseCase;
    private final UpdateChatTitleUseCase updateChatTitleUseCase;
    private final CopyChatUseCase copyChatUseCase;

    @PostMapping
    ResponseEntity<CreateChatResponse> createChat(
            @Auth Long memberId,
            @RequestBody CreateChatRequest request
    ) {
        CreateChatResult answer = createChatUseCase.createChat(
                new CreateChatCommand(memberId, request.message())
        );
        return ResponseEntity.status(CREATED.value())
                .body(new CreateChatResponse(answer.chatId(), answer.answer()));
    }

    @PostMapping("/{id}")
    ResponseEntity<AskResponse> ask(
            @PathVariable("id") Long chatId,
            @Auth Long memberId,
            @RequestBody AskRequest request
    ) {
        AskResult result = askUseCase.ask(chatId, new AskCommand(memberId, request.message()));
        return ResponseEntity.status(CREATED.value())
                .body(new AskResponse(chatId, result.answer()));
    }

    @PatchMapping("/{id}")
    ResponseEntity<Void> updateTitle(
            @PathVariable("id") Long chatId,
            @Auth Long memberId,
            @RequestBody UpdateChatTitleRequest request
    ) {
        updateChatTitleUseCase.updateTitle(
                new UpdateChatTitleCommand(memberId, chatId, request.title())
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/copy/{id}")
    ResponseEntity<CopyChatResponse> copy(
            @PathVariable("id") Long chatId,
            @Auth Long memberId
    ) {
        Long copiedId = copyChatUseCase.copy(
                new CopyCommand(chatId, memberId)
        );
        return ResponseEntity.status(CREATED)
                .body(new CopyChatResponse(copiedId));
    }
}
