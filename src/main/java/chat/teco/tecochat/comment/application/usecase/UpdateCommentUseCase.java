package chat.teco.tecochat.comment.application.usecase;

public interface UpdateCommentUseCase {

    void update(UpdateCommentCommand command);

    record UpdateCommentCommand(
            Long commentId,
            Long memberId,
            String content
    ) {
    }
}
