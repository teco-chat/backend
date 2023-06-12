package chat.teco.tecochat.chat.presentation.chat.api.response;

public record CreateChatResponse(
        Long chatId,
        String content
) {
}
