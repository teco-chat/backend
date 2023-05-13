package chat.teco.tecochat.like.chatlike.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PushChatLikeService {

    private final ChatLikeRepository chatLikeRepository;
    private final ChatRepository chatRepository;

    public PushChatLikeService(final ChatLikeRepository chatLikeRepository, final ChatRepository chatRepository) {
        this.chatLikeRepository = chatLikeRepository;
        this.chatRepository = chatRepository;
    }

    public void pushLike(final PushChatLikeCommand command) {
        final Optional<Chat> optionalChat = chatRepository.findById(command.chatId());
        if (optionalChat.isEmpty()) {
            throw new ChatException(NOT_FOUND_CHAT);
        }

        final Chat chat = optionalChat.get();
        chatLikeRepository.findByMemberIdAndChatId(command.memberId, command.chatId)
                .ifPresentOrElse(
                        chatLike -> deleteLike(chat, chatLike),
                        () -> createLike(chat, command.toDomain())
                );
    }

    private void deleteLike(final Chat chat, final ChatLike chatLike) {
        chatLikeRepository.delete(chatLike);
        chat.decreaseLike();
    }

    private void createLike(final Chat chat, final ChatLike chatLike) {
        chatLikeRepository.save(chatLike);
        chat.increaseLike();
    }

    public record PushChatLikeCommand(
            Long memberId,
            Long chatId
    ) {
        public ChatLike toDomain() {
            return new ChatLike(memberId, chatId);
        }
    }
}
