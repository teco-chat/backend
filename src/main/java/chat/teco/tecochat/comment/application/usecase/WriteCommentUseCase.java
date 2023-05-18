package chat.teco.tecochat.comment.application.usecase;

import chat.teco.tecochat.comment.domain.Comment;

public interface WriteCommentUseCase {

    Long write(WriteCommentCommand command);

    record WriteCommentCommand(
            Long chatId,
            Long memberId,
            String content
    ) {
        public Comment toDomain() {
            return new Comment(chatId, memberId, content);
        }
    }
}
