package chat.teco.tecochat.domain.comment

import chat.teco.tecochat.UPDATED_CONTENT
import chat.teco.tecochat.createComment
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class CommentTest : StringSpec({

    "작성자는 댓글을 수정할 수 있다" {
        val memberId = 1L
        val comment = createComment(memberId = memberId)

        comment.update(memberId, UPDATED_CONTENT)

        comment.content shouldBe UPDATED_CONTENT
    }

    "작성자가 아닌 경우 댓글을 수정할 수 없다" {
        val comment = createComment(memberId = 1L)

        shouldThrow<IllegalStateException> {
            comment.update(2L, UPDATED_CONTENT)
        }
    }

    "작성자인 경우 댓글을 삭제할 수 있다" {
        val comment = createComment(memberId = 1L)

        shouldNotThrowAny {
            comment.validateDelete(1L)
        }
    }

    "작성자가 아닌 경우 댓글을 삭제할 수 없다" {
        val comment = createComment(memberId = 1L)

        shouldThrow<IllegalStateException> {
            comment.validateDelete(2L)
        }
    }
})
