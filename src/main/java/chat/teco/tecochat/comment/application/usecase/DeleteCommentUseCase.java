package chat.teco.tecochat.comment.application.usecase;

public interface DeleteCommentUseCase {

    void delete(DeleteCommentCommand command);

    record DeleteCommentCommand(
            Long commentId,
            Long memberId
    ) {
    }
}
