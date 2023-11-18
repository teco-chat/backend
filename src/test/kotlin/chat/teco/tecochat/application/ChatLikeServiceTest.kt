package chat.teco.tecochat.application

import chat.teco.tecochat.MEMBER_ID
import chat.teco.tecochat.createChat
import chat.teco.tecochat.createChatLike
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class ChatLikeServiceTest : BehaviorSpec({

    val chatLikeRepository = mockk<ChatLikeRepository>()
    val chatRepository = mockk<ChatRepository>()

    val chatLikeService = ChatLikeService(chatLikeRepository, chatRepository)

    Given("이미 좋아요를 누른 경우") {
        val chat = createChat(likeCount = 1)
        val chatLike = createChatLike()
        every { chatRepository.getById(any()) } returns chat
        every { chatLikeRepository.findByMemberIdAndChatId(any(), any()) } returns chatLike
        every { chatLikeRepository.delete(any()) } returns Unit

        When("좋아요를 누를 때") {
            chatLikeService.pushLike(MEMBER_ID, chat.id)

            Then("좋아요가 취소된다") {
                chat.likeCount shouldBe 0
                verify(exactly = 1) { chatLikeRepository.delete(any()) }
            }
        }
    }

    Given("좋아요를 누르지 않은 경우") {
        val chat = createChat()
        every { chatRepository.getById(any()) } returns chat
        every { chatLikeRepository.findByMemberIdAndChatId(any(), any()) } returns null
        every { chatLikeRepository.save(any()) } returns createChatLike()

        When("좋아요를 누를 때") {
            chatLikeService.pushLike(MEMBER_ID, chat.id)

            Then("좋아요가 등록된다") {
                chat.likeCount shouldBe 1
                verify(exactly = 1) { chatLikeRepository.save(any()) }
            }
        }
    }
})
