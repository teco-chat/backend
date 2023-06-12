package chat.teco.tecochat.comment.application.dto;

public record DeleteCommentCommand(
        Long commentId,
        Long memberId
) {
}
