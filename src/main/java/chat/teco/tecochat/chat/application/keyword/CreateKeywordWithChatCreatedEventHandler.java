package chat.teco.tecochat.chat.application.keyword;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.chat.event.ChatCreatedEvent;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.domain.keyword.KeywordExtractor;
import chat.teco.tecochat.chat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.exception.keyword.KeywordException;
import chat.teco.tecochat.common.event.BaseEventHistory;
import chat.teco.tecochat.common.event.EventHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionTemplate;

@RequiredArgsConstructor
@Component
public class CreateKeywordWithChatCreatedEventHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EventHistoryRepository eventHistoryRepository;
    private final ChatRepository chatRepository;
    private final KeywordRepository keywordRepository;
    private final TransactionTemplate transactionTemplate;
    private final KeywordExtractor keywordExtractor;

    @Async
    @Retryable(
            retryFor = KeywordException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1500)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ChatCreatedEvent event) {
        Chat chat = chatRepository.findWithQuestionAndAnswersById(event.chatId())
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
        List<Keyword> keywords = keywordExtractor.extractKeywords(chat);
        transactionTemplate.executeWithoutResult(status -> {
            keywordRepository.saveAll(keywords);
            BaseEventHistory history = event.history();
            history.process();
            eventHistoryRepository.save(history);
        });
    }

    @Recover
    public void recover(ChatCreatedEvent event) {
        log.warn("키워드 추출에 실패했습니다. chatId = {}", event.chatId());
        BaseEventHistory history = event.history();
        eventHistoryRepository.save(history);
    }
}
