package chat.teco.tecochat.like.chatlike.application.dto;

import chat.teco.tecochat.like.chatlike.domain.ChatLike;

public record PushChatLikeCommand(
        Long memberId,
        Long chatId
) {
    public ChatLike toDomain() {
        return new ChatLike(memberId, chatId);
    }
}
