package chat.teco.tecochat.comment.application.dto;

public record UpdateCommentCommand(
        Long commentId,
        Long memberId,
        String content
) {
}
