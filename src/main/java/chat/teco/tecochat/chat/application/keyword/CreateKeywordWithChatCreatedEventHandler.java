package chat.teco.tecochat.chat.application.keyword;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.domain.event.ChatCreatedEvent;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.domain.keyword.KeywordExtractor;
import chat.teco.tecochat.chat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.chat.exception.KeywordException;
import chat.teco.tecochat.common.event.BaseEventHistory;
import chat.teco.tecochat.common.event.EventHandler;
import chat.teco.tecochat.common.event.EventHistoryRepository;
import java.util.List;
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

@Component
public class CreateKeywordWithChatCreatedEventHandler extends EventHandler<ChatCreatedEvent> {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ChatRepository chatRepository;
    private final KeywordRepository keywordRepository;
    private final TransactionTemplate transactionTemplate;
    private final KeywordExtractor keywordExtractor;

    public CreateKeywordWithChatCreatedEventHandler(final EventHistoryRepository eventHistoryRepository,
                                                    final ChatRepository chatRepository,
                                                    final KeywordRepository keywordRepository,
                                                    final TransactionTemplate transactionTemplate,
                                                    final KeywordExtractor keywordExtractor) {
        super(eventHistoryRepository);
        this.chatRepository = chatRepository;
        this.keywordRepository = keywordRepository;
        this.transactionTemplate = transactionTemplate;
        this.keywordExtractor = keywordExtractor;
    }

    @Async
    @Retryable(
            retryFor = KeywordException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1500)
    )
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(final ChatCreatedEvent event) {
        final Chat chat = chatRepository.findWithQuestionAndAnswersById(event.chatId())
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
        final List<Keyword> keywords = keywordExtractor.extractKeywords(chat);
        transactionTemplate.executeWithoutResult(status -> {
            keywordRepository.saveAll(keywords);
            final BaseEventHistory history = event.history();
            history.process();
            eventHistoryRepository.save(history);
        });
    }

    @Recover
    public void recover(final ChatCreatedEvent event) {
        log.warn("키워드 추출에 실패했습니다. chatId = {}", event.chatId());
        final BaseEventHistory history = event.history();
        eventHistoryRepository.save(history);
    }
}
