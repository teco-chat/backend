package chat.teco.tecochat.application

import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.ChatCopiedEvent
import chat.teco.tecochat.domain.chat.ChatCreatedEvent
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.keyword.Keyword
import chat.teco.tecochat.domain.keyword.KeywordExtractor
import chat.teco.tecochat.domain.keyword.KeywordRepository
import chat.teco.tecochat.support.domain.EventHistoryRepository
import org.springframework.context.event.EventListener
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener
import org.springframework.transaction.support.TransactionTemplate

@Component
class KeywordService(
    var chatRepository: ChatRepository,
    var keywordRepository: KeywordRepository,
    var keywordExtractor: KeywordExtractor,
    var transactionTemplate: TransactionTemplate,
    var eventHistoryRepository: EventHistoryRepository,
) {

    @Transactional
    @EventListener(classes = [ChatCopiedEvent::class])
    fun handleChatCopiedEvent(event: ChatCopiedEvent) {
        val copiedChat: Chat = chatRepository.getById(event.copiedChatId)
        val copiedKeywords: List<Keyword> = keywordRepository.findAllByChatId(event.originChatId)
            .map { it.copy(copiedChat) }
        keywordRepository.saveAll(copiedKeywords)
    }

    @Async
    @Retryable(retryFor = [RuntimeException::class], maxAttempts = 3, backoff = Backoff(delay = 1500))
    @TransactionalEventListener(classes = [ChatCreatedEvent::class], phase = TransactionPhase.AFTER_COMMIT)
    fun handleChatCreatedEvent(event: ChatCreatedEvent) {
        val chat = chatRepository.findWithQuestionAndAnswersById(event.chatId)
            .orElseThrow { NoSuchElementException("채팅이 존재하지 않습니다.") }
        val keywords: List<Keyword> = keywordExtractor.extractKeywords(chat)
        transactionTemplate.executeWithoutResult { status ->
            keywordRepository.saveAll(keywords)
            eventHistoryRepository.save(event.processedHistory())
        }
    }

    @Recover
    fun recover(event: ChatCreatedEvent) {
        eventHistoryRepository.save(event.history())
    }
}
