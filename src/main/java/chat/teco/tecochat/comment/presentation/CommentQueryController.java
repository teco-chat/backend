package chat.teco.tecochat.comment.presentation;

import chat.teco.tecochat.comment.query.usecase.QueryAllCommentByChatIdUseCase;
import chat.teco.tecochat.comment.query.usecase.QueryAllCommentByChatIdUseCase.CommentQueryDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentQueryController {

    private final QueryAllCommentByChatIdUseCase queryAllCommentByChatIdUseCase;

    @GetMapping
    ResponseEntity<List<CommentQueryDto>> findAllByChatId(
            @RequestParam("chatId") Long chatId
    ) {
        return ResponseEntity.ok(queryAllCommentByChatIdUseCase.findAllByChatId(chatId));
    }
}
