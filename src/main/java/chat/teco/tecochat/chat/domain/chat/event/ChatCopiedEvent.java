package chat.teco.tecochat.chat.domain.chat.event;

public record ChatCopiedEvent(
        Long originChatId,
        Long copiedChatId
) {
}
