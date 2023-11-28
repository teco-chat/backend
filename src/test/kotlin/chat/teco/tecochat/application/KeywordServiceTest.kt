package chat.teco.tecochat.application

import chat.teco.tecochat.common.FakeTransactionTemplate
import chat.teco.tecochat.createChat
import chat.teco.tecochat.domain.chat.ChatCopiedEvent
import chat.teco.tecochat.domain.chat.ChatCreatedEvent
import chat.teco.tecochat.domain.chat.ChatCreatedEventHistory
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.getByIdOrThrow
import chat.teco.tecochat.domain.keyword.Keyword
import chat.teco.tecochat.domain.keyword.KeywordExtractor
import chat.teco.tecochat.domain.keyword.KeywordRepository
import chat.teco.tecochat.support.domain.EventHistoryRepository
import chat.teco.tecochat.support.test.afterRootTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime
import java.util.Optional

class KeywordServiceTest : StringSpec({

    val chatRepository = mockk<ChatRepository>()
    val keywordRepository = mockk<KeywordRepository>()
    val keywordExtractor = mockk<KeywordExtractor>()
    val transactionTemplate = FakeTransactionTemplate.spied()
    val eventHistoryRepository = mockk<EventHistoryRepository>()

    val keywordService = KeywordService(
        chatRepository,
        keywordRepository,
        keywordExtractor,
        transactionTemplate,
        eventHistoryRepository
    )

    "채팅 복제 이벤트를 구독하여 기존 채팅의 키워드도 복제한다" {
        val originChat = createChat(id = 1L)
        val copiedChat = createChat(id = 2L)
        every { chatRepository.getByIdOrThrow(copiedChat.id) } returns copiedChat
        every { keywordRepository.findAllByChatId(originChat.id) } returns listOf(Keyword("키워드", originChat))
        every { keywordRepository.saveAll(any<List<Keyword>>()) } returns listOf(Keyword("키워드", copiedChat))

        keywordService.handleChatCopiedEvent(ChatCopiedEvent(originChat.id, copiedChat.id))

        verify(exactly = 1) { keywordRepository.saveAll(any<List<Keyword>>()) }
    }

    "키워드 추출시 오류가 발생하면 저장하지 않는다" {
        val chat = createChat()
        every { chatRepository.findWithQuestionAndAnswersById(chat.id) } returns Optional.of(chat)
        every { keywordExtractor.extractKeywords(chat) } throws IllegalStateException("키워드 추출에 실패했습니다.")

        shouldThrow<IllegalStateException> {
            keywordService.handleChatCreatedEvent(ChatCreatedEvent(chat.id, LocalDateTime.now()))
        }

        verify(exactly = 0) { keywordRepository.saveAll(any<List<Keyword>>()) }
    }

    "채팅 생성 이벤트를 구독하여 해당 채팅의 키워드를 추출하여 저장한다" {
        val chat = createChat()
        every { chatRepository.findWithQuestionAndAnswersById(chat.id) } returns Optional.of(chat)
        every { keywordExtractor.extractKeywords(chat) } returns listOf(Keyword("키워드", chat))
        every { keywordRepository.saveAll(any<List<Keyword>>()) } returns listOf(Keyword("키워드", chat))
        every { eventHistoryRepository.save(any()) } returns ChatCreatedEventHistory(LocalDateTime.now(), chat.id)

        keywordService.handleChatCreatedEvent(ChatCreatedEvent(chat.id, LocalDateTime.now()))

        verify(exactly = 1) { keywordRepository.saveAll(any<List<Keyword>>()) }
    }

    afterRootTest {
        clearAllMocks()
    }
})
