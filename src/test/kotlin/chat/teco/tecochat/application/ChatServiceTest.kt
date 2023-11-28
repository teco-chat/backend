package chat.teco.tecochat.application

import chat.teco.tecochat.createChat
import chat.teco.tecochat.createUpdateChatTitleRequest
import chat.teco.tecochat.domain.chat.ChatCopiedEvent
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.getByIdOrThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.context.ApplicationEventPublisher

class ChatServiceTest : FeatureSpec({

    val chatRepository = mockk<ChatRepository>()
    val applicationEventPublisher = mockk<ApplicationEventPublisher>(relaxed = true)

    val chatService = ChatService(chatRepository, applicationEventPublisher)

    feature("채팅을 복사할 때") {

        scenario("채팅을 복사한다") {
            val chat = createChat(id = 1L)
            val copiedChat = createChat(id = 2L)
            every { chatRepository.getByIdOrThrow(any()) } returns chat
            every { chatRepository.save(any()) } returns copiedChat

            chatService.copy(2L, chat.id)

            verify(exactly = 1) { chatRepository.save(any()) }
            verify(exactly = 1) { applicationEventPublisher.publishEvent(ChatCopiedEvent(chat.id, copiedChat.id)) }
        }
    }

    feature("채팅의 제목을 수정할 때") {

        scenario("제목을 수정한다") {
            val chat = createChat(id = 1L)
            val request = createUpdateChatTitleRequest()
            every { chatRepository.getByIdOrThrow(any()) } returns chat

            chatService.updateTitle(1L, chat.id, request)

            chat.title shouldBe request.title
        }

        scenario("채팅을 진행한 사람이 아닌 경우 예외가 발생한다") {
            val chat = createChat(id = 1L, memberId = 1L)
            val request = createUpdateChatTitleRequest()
            every { chatRepository.getByIdOrThrow(any()) } returns chat

            shouldThrow<IllegalStateException> {
                chatService.updateTitle(2L, chat.id, request)
            }
        }
    }
})
