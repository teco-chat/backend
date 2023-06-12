package chat.teco.tecochat.comment.application.dto;

import chat.teco.tecochat.comment.domain.Comment;

public record WriteCommentCommand(
        Long chatId,
        Long memberId,
        String content
) {
    public Comment toDomain() {
        return new Comment(chatId, memberId, content);
    }
}
