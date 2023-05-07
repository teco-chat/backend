package chat.teco.tecochat.comment.presentation.request;

public record WriteCommentRequest(
        Long chatId,
        String content
) {
}
