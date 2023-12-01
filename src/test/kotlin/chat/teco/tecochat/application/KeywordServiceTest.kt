package chat.teco.tecochat.application

import chat.teco.tecochat.common.FakeTransactionTemplate
import chat.teco.tecochat.createChat
import chat.teco.tecochat.domain.chat.Answer
import chat.teco.tecochat.domain.chat.ChatCopiedEvent
import chat.teco.tecochat.domain.chat.ChatCreatedEvent
import chat.teco.tecochat.domain.chat.ChatCreatedEventHistory
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.getByIdOrThrow
import chat.teco.tecochat.domain.chat.getWithQuestionAndAnswersByIdOrThrow
import chat.teco.tecochat.domain.keyword.Keyword
import chat.teco.tecochat.domain.keyword.KeywordRepository
import chat.teco.tecochat.infra.gpt.GptClient
import chat.teco.tecochat.support.domain.EventHistoryRepository
import chat.teco.tecochat.support.test.afterRootTest
import io.kotest.core.spec.style.StringSpec
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class KeywordServiceTest : StringSpec({

    val chatRepository = mockk<ChatRepository>()
    val keywordRepository = mockk<KeywordRepository>()
    val transactionTemplate = FakeTransactionTemplate.spied()
    val eventHistoryRepository = mockk<EventHistoryRepository>()
    val gptClient = mockk<GptClient>()

    val keywordService = KeywordService(
        chatRepository,
        keywordRepository,
        transactionTemplate,
        eventHistoryRepository,
        gptClient
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

    "채팅 생성 이벤트를 구독하여 해당 채팅의 키워드를 추출하여 저장한다" {
        val chat = createChat()
        every { chatRepository.getWithQuestionAndAnswersByIdOrThrow(chat.id) } returns chat
        every { gptClient.ask(any()) } returns Answer.answer("#키워드1 #키워드2 #키워드3")
        every { keywordRepository.saveAll(any<List<Keyword>>()) } returns listOf(Keyword("키워드", chat))
        every { eventHistoryRepository.save(any()) } returns ChatCreatedEventHistory(LocalDateTime.now(), chat.id)

        keywordService.handleChatCreatedEvent(ChatCreatedEvent(chat.id, LocalDateTime.now()))

        verify(exactly = 1) { keywordRepository.saveAll(any<List<Keyword>>()) }
    }

    afterRootTest {
        clearAllMocks()
    }
})
