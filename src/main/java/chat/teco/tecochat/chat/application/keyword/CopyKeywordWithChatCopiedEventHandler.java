package chat.teco.tecochat.chat.application.keyword;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.domain.chat.ChatCopiedEvent;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chat.KeywordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Component
public class CopyKeywordWithChatCopiedEventHandler {

    private final ChatRepository chatRepository;
    private final KeywordRepository keywordRepository;

    @EventListener(classes = {ChatCopiedEvent.class})
    public void handle(ChatCopiedEvent event) {
        Chat copiedChat = chatRepository.getById(event.getCopiedChatId());
        List<Keyword> copiedKeywords = keywordRepository.findAllByChatId(event.getOriginChatId())
                .stream()
                .map(keyword -> keyword.copy(copiedChat))
                .toList();
        keywordRepository.saveAll(copiedKeywords);
    }
}
