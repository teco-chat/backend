package chat.teco.tecochat.application

import chat.teco.tecochat.createChat
import chat.teco.tecochat.createComment
import chat.teco.tecochat.createMember
import chat.teco.tecochat.createUpdateCommentRequest
import chat.teco.tecochat.createWriteCommentRequest
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.getByIdOrThrow
import chat.teco.tecochat.domain.comment.CommentRepository
import chat.teco.tecochat.domain.comment.getByIdOrThrow
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.MemberRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify

class CommentServiceTest : FeatureSpec({

    val chatRepository = mockk<ChatRepository>()
    val commentRepository = mockk<CommentRepository>()
    val memberRepository = mockk<MemberRepository>()

    val commentService = CommentService(chatRepository, commentRepository, memberRepository)

    feature("댓글을 등록할 때") {
        scenario("채팅이 없다면 예외가 발생한다") {
            every { chatRepository.getByIdOrThrow(any()) } throws NoSuchElementException()

            shouldThrow<NoSuchElementException> {
                commentService.write(1L, createWriteCommentRequest())
            }
        }

        scenario("댓글이 정상적으로 등록된다") {
            val chat = createChat()
            every { chatRepository.getByIdOrThrow(any()) } returns chat
            every { commentRepository.save(any()) } returns createComment()

            commentService.write(1L, createWriteCommentRequest(chatId = chat.id))

            verify(exactly = 1) { commentRepository.save(any()) }
            chat.commentCount shouldBe 1
        }
    }

    feature("댓글을 수정할 때") {
        scenario("댓글의 작성자가 아니라면 예외가 발생한다") {
            val comment = createComment(memberId = 1L)
            every { commentRepository.getByIdOrThrow(any()) } returns comment

            shouldThrow<IllegalStateException> {
                commentService.update(2L, comment.id, createUpdateCommentRequest())
            }
        }

        scenario("댓글이 정상적으로 수정된다") {
            val comment = createComment()
            val updateCommentRequest = createUpdateCommentRequest()
            every { commentRepository.getByIdOrThrow(any()) } returns comment

            commentService.update(1L, comment.id, updateCommentRequest)

            comment.content shouldBe updateCommentRequest.content
        }
    }

    feature("댓글을 삭제할 때") {
        scenario("채팅이 없다면 예외가 발생한다") {
            every { chatRepository.getByIdOrThrow(any()) } throws NoSuchElementException()
            every { commentRepository.getByIdOrThrow(any()) } returns createComment()

            shouldThrow<NoSuchElementException> {
                commentService.delete(1L, 1L)
            }
        }

        scenario("댓글의 작성자가 아니라면 예외가 발생한다") {
            val comment = createComment(memberId = 1L)
            every { chatRepository.getByIdOrThrow(any()) } returns createChat()
            every { commentRepository.getByIdOrThrow(any()) } returns comment

            shouldThrow<IllegalStateException> {
                commentService.delete(2L, comment.id)
            }
        }

        scenario("댓글이 정상적으로 삭제된다") {
            val chat = createChat(commentCount = 1)
            val comment = createComment()
            every { chatRepository.getByIdOrThrow(any()) } returns chat
            every { commentRepository.getByIdOrThrow(any()) } returns comment
            every { commentRepository.delete(any()) } just runs

            commentService.delete(comment.memberId, comment.id)

            verify(exactly = 1) { commentRepository.delete(any()) }
            chat.commentCount shouldBe 0
        }
    }

    feature("댓글을 조회할 때") {
        scenario("댓글이 정상적으로 조회된다") {
            val member1 = createMember("herb", Course.BACKEND, 1L)
            val member2 = createMember("mallang", Course.FRONTEND, 2L)
            val comment1 = createComment(memberId = member1.id)
            val comment2 = createComment(memberId = member2.id)
            every { commentRepository.findAllByChatId(any()) } returns listOf(comment1, comment2)
            every { memberRepository.findAllById(any()) } returns listOf(member1, member2)

            val result = commentService.findAllByChatId(1L)

            result shouldContainAll listOf(
                CommentResponse(comment1.id, member1.name, member1.course, comment1.content, comment1.createdAt),
                CommentResponse(comment2.id, member2.name, member2.course, comment2.content, comment2.createdAt)
            )
        }
    }
})
