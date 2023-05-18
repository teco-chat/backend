package chat.teco.tecochat.like.chatlike.application.service;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatLikeService implements PushChatLikeUseCase {

    private final ChatLikeRepository chatLikeRepository;
    private final ChatRepository chatRepository;

    public void pushLike(PushChatLikeCommand command) {
        Optional<Chat> optionalChat = chatRepository.findById(command.chatId());
        if (optionalChat.isEmpty()) {
            throw new ChatException(NOT_FOUND_CHAT);
        }
        Chat chat = optionalChat.get();
        chatLikeRepository.findByMemberIdAndChatId(command.memberId(), command.chatId())
                .ifPresentOrElse(
                        chatLike -> deleteLike(chat, chatLike),
                        () -> createLike(chat, command.toDomain())
                );
    }

    private void deleteLike(Chat chat, ChatLike chatLike) {
        chatLikeRepository.delete(chatLike);
        chat.decreaseLike();
    }

    private void createLike(Chat chat, ChatLike chatLike) {
        chatLikeRepository.save(chatLike);
        chat.increaseLike();
    }
}
