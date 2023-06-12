package chat.teco.tecochat.comment.presentation;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

import chat.teco.tecochat.auth.Auth;
import chat.teco.tecochat.comment.application.CommentService;
import chat.teco.tecochat.comment.application.dto.DeleteCommentCommand;
import chat.teco.tecochat.comment.application.dto.UpdateCommentCommand;
import chat.teco.tecochat.comment.application.dto.WriteCommentCommand;
import chat.teco.tecochat.comment.presentation.request.UpdateCommentRequest;
import chat.teco.tecochat.comment.presentation.request.WriteCommentRequest;
import chat.teco.tecochat.comment.presentation.response.CreatedIdResponse;
import chat.teco.tecochat.common.util.UriUtil;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    ResponseEntity<CreatedIdResponse> write(
            @Auth Long memberId,
            @RequestBody WriteCommentRequest request
    ) {
        Long id = commentService.write(
                new WriteCommentCommand(request.chatId(), memberId, request.content())
        );
        URI uri = UriUtil.buildURI("/{id}", id);
        return created(uri).body(new CreatedIdResponse(id));
    }

    @PatchMapping("/{id}")
    ResponseEntity<Void> update(
            @PathVariable("id") Long commentId,
            @Auth Long memberId,
            @RequestBody UpdateCommentRequest request) {
        commentService.update(
                new UpdateCommentCommand(commentId, memberId, request.content())
        );
        return ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @PathVariable("id") Long commentId,
            @Auth Long memberId
    ) {
        commentService.delete(
                new DeleteCommentCommand(commentId, memberId)
        );
        return ok().build();
    }
}
