package chat.teco.tecochat.chat.application.chat.dto;

public record CopyCommand(
        Long chatId,
        Long memberId
) {
}
