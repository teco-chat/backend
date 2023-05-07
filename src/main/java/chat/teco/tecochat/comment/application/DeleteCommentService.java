package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;

import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.execption.CommentException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DeleteCommentService {

    private final CommentRepository commentRepository;

    public DeleteCommentService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public void delete(final DeleteCommentCommand command) {
        final Comment comment = commentRepository.findById(command.commentId())
                .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

        comment.validateDelete(command.memberId());
        commentRepository.delete(comment);
    }

    public record DeleteCommentCommand(
            Long commentId,
            Long memberId
    ) {
    }
}
