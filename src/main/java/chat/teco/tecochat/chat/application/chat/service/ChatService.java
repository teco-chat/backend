package chat.teco.tecochat.chat.application.chat.service;

import chat.teco.tecochat.chat.application.chat.usecase.CopyChatUseCase;
import chat.teco.tecochat.chat.application.chat.usecase.UpdateChatTitleUseCase;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.chat.event.ChatCopiedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatService implements
        UpdateChatTitleUseCase,
        CopyChatUseCase {

    private final ChatRepository chatRepository;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @Override
    public void updateTitle(UpdateChatTitleCommand command) {
        Chat chat = chatRepository.getById(command.chatId());
        chat.updateTitle(command.memberId(), command.title());
    }

    @Transactional
    @Override
    public Long copy(CopyCommand command) {
        Chat chat = chatRepository.getById(command.chatId());
        Chat copied = chat.copy(command.memberId());
        Long copiedChatId = chatRepository.save(copied).id();
        publisher.publishEvent(new ChatCopiedEvent(chat.id(), copiedChatId));
        return copiedChatId;
    }
}
