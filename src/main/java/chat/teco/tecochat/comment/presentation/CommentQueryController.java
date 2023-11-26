package chat.teco.tecochat.comment.presentation;

import chat.teco.tecochat.application.CommentQueryResponse;
import chat.teco.tecochat.application.CommentService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
public class CommentQueryController {

    private final CommentService commentService;

    public CommentQueryController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    ResponseEntity<List<CommentQueryResponse>> findAllByChatId(
            @RequestParam("chatId") Long chatId
    ) {
        return ResponseEntity.ok(commentService.findAllByChatId(chatId));
    }
}
