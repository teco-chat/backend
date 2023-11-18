package chat.teco.tecochat.application

import chat.teco.tecochat.createChat
import chat.teco.tecochat.createComment
import chat.teco.tecochat.createUpdateCommentRequest
import chat.teco.tecochat.createWriteCommentRequest
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.comment.CommentRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class CommentServiceTest : FeatureSpec({

    val chatRepository = mockk<ChatRepository>()
    val commentRepository = mockk<CommentRepository>()

    val commentService = CommentService(chatRepository, commentRepository)

    feature("댓글을 등록할 때") {
        scenario("채팅이 없다면 예외가 발생한다") {
            every { chatRepository.getById(any()) } throws NoSuchElementException()

            shouldThrow<NoSuchElementException> {
                commentService.write(1L, createWriteCommentRequest())
            }
        }

        scenario("댓글이 정상적으로 등록된다") {
            val chat = createChat()
            every { chatRepository.getById(any()) } returns chat
            every { commentRepository.save(any()) } returns createComment()

            commentService.write(1L, createWriteCommentRequest(chatId = chat.id()))

            verify(exactly = 1) { commentRepository.save(any()) }
            chat.commentCount() shouldBe 1
        }
    }

    feature("댓글을 수정할 때") {
        scenario("댓글의 작성자가 아니라면 예외가 발생한다") {
            val comment = createComment(memberId = 1L)
            every { commentRepository.getById(any()) } returns comment

            shouldThrow<IllegalStateException> {
                commentService.update(2L, comment.id(), createUpdateCommentRequest())
            }
        }

        scenario("댓글이 정상적으로 수정된다") {
            val comment = createComment()
            val updateCommentRequest = createUpdateCommentRequest()
            every { commentRepository.getById(any()) } returns comment

            commentService.update(1L, comment.id(), updateCommentRequest)

            comment.content shouldBe updateCommentRequest.content
        }
    }

    feature("댓글을 삭제할 때") {
        scenario("채팅이 없다면 예외가 발생한다") {
            every { chatRepository.getById(any()) } throws NoSuchElementException()
            every { commentRepository.getById(any()) } returns createComment()

            shouldThrow<NoSuchElementException> {
                commentService.delete(1L, 1L)
            }
        }

        scenario("댓글의 작성자가 아니라면 예외가 발생한다") {
            val comment = createComment(memberId = 1L)
            every { chatRepository.getById(any()) } returns createChat()
            every { commentRepository.getById(any()) } returns comment

            shouldThrow<IllegalStateException> {
                commentService.delete(2L, comment.id())
            }
        }

        scenario("댓글이 정상적으로 삭제된다") {
            val chat = createChat(commentCount = 1L)
            val comment = createComment()
            every { chatRepository.getById(any()) } returns chat
            every { commentRepository.getById(any()) } returns comment
            every { commentRepository.delete(any()) } just runs

            commentService.delete(comment.memberId, comment.id())

            verify(exactly = 1) { commentRepository.delete(any()) }
            chat.commentCount() shouldBe 0
        }
    }
})
