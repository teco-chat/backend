package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;

import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.execption.CommentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UpdateCommentService {

    private final CommentRepository commentRepository;

    public UpdateCommentService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void update(final UpdateCommentCommand command) {
        final Comment comment = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

        comment.update(command.memberId(), command.content());
    }

    public record UpdateCommentCommand(
            Long commentId,
            Long memberId,
            String content
    ) {
    }
}
