package chat.teco.tecochat.like.chatlike.application.usecase;

import chat.teco.tecochat.like.chatlike.domain.ChatLike;

public interface PushChatLikeUseCase {

    void pushLike(PushChatLikeCommand command);

    record PushChatLikeCommand(
            Long memberId,
            Long chatId
    ) {
        public ChatLike toDomain() {
            return new ChatLike(memberId, chatId);
        }
    }
}
