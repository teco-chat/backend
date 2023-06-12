package chat.teco.tecochat.chat.application.chat.dto;

public record UpdateChatTitleCommand(
        Long memberId,
        Long chatId,
        String title
) {
}
