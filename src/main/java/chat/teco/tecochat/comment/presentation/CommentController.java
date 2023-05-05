package chat.teco.tecochat.comment.presentation;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.ok;

import chat.teco.tecochat.comment.application.DeleteCommentService;
import chat.teco.tecochat.comment.application.DeleteCommentService.DeleteCommentCommand;
import chat.teco.tecochat.comment.application.UpdateCommentService;
import chat.teco.tecochat.comment.application.UpdateCommentService.UpdateCommentCommand;
import chat.teco.tecochat.comment.application.WriteCommentService;
import chat.teco.tecochat.comment.application.WriteCommentService.WriteCommentCommand;
import chat.teco.tecochat.comment.presentation.request.UpdateCommentRequest;
import chat.teco.tecochat.comment.presentation.request.WriteCommentRequest;
import chat.teco.tecochat.member.presentation.argumentresolver.Auth;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final WriteCommentService writeCommentService;
    private final UpdateCommentService updateCommentService;
    private final DeleteCommentService deleteCommentService;

    public CommentController(final WriteCommentService writeCommentService,
                             final UpdateCommentService updateCommentService,
                             final DeleteCommentService deleteCommentService) {
        this.writeCommentService = writeCommentService;
        this.updateCommentService = updateCommentService;
        this.deleteCommentService = deleteCommentService;
    }

    @PostMapping
    ResponseEntity<Void> write(
            @Auth final Long memberId,
            @RequestBody final WriteCommentRequest request
    ) {
        final Long id = writeCommentService.write(
                new WriteCommentCommand(request.chatId(), memberId, request.content())
        );
        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return created(uri).build();
    }

    @PatchMapping("/{id}")
    ResponseEntity<Void> update(
            @PathVariable("id") final Long commentId,
            @Auth final Long memberId,
            @RequestBody final UpdateCommentRequest request) {
        updateCommentService.update(
                new UpdateCommentCommand(commentId, memberId, request.content())
        );
        return ok().build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(
            @PathVariable("id") final Long commentId,
            @Auth final Long memberId
    ) {
        deleteCommentService.delete(
                new DeleteCommentCommand(commentId, memberId)
        );
        return ok().build();
    }
}
