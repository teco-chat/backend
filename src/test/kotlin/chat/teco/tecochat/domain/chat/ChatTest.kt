package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.UPDATED_TITLE
import chat.teco.tecochat.createChat
import chat.teco.tecochat.createQuestionAndAnswer
import io.kotest.assertions.extracting
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class ChatTest : StringSpec({

    "QnA를 추가할 수 있다" {
        val chat = createChat()
        val questionAndAnswer = createQuestionAndAnswer()

        chat.addQuestionAndAnswer(questionAndAnswer)

        chat.questionAndAnswers.questionAndAnswers[0] shouldBe questionAndAnswer
    }

    "마지막 3개의 질문과 답변을 반환한다" {
        val chat = createChat(
            questionAndAnswers = listOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2"),
                createQuestionAndAnswer("질문3", "답변3"),
                createQuestionAndAnswer("질문4", "답변4")
            )
        )

        val result = chat.last3QuestionAndAnswers()

        extracting(result.questionAndAnswers) { Pair(question.question, answer.answer) }
            .shouldContainExactly(
                Pair("질문2", "답변2"),
                Pair("질문3", "답변3"),
                Pair("질문4", "답변4"),
            )
    }

    "질문과 답변이 3개 보다 적다면 전부 반환한다" {
        val chat = createChat(
            questionAndAnswers = listOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2")
            )
        )

        val result = chat.last3QuestionAndAnswers()

        extracting(result.questionAndAnswers) { Pair(question.question, answer.answer) }
            .shouldContainExactly(
                Pair("질문1", "답변1"),
                Pair("질문2", "답변2"),
            )
    }

    "작성자가 아니라면 제목을 수정할 때 예외를 던진다" {
        val chat = createChat(memberId = 1L)

        shouldThrow<IllegalStateException> {
            chat.updateTitle(2L, UPDATED_TITLE)
        }
    }

    "작성자라면 제목을 수정할 수 있다" {
        val memberId = 1L
        val chat = createChat(memberId = memberId)

        chat.updateTitle(memberId, UPDATED_TITLE)

        chat.title shouldBe UPDATED_TITLE
    }

    "채팅 복사시 진행한 채팅을 모두 복사한다" {
        val chat = createChat(
            questionAndAnswers = listOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2"),
            )
        )

        val result = chat.copy(1L)

        extracting(result.questionAndAnswers.questionAndAnswers) { Pair(question.question, answer.answer) }
            .shouldContainExactly(
                Pair("질문1", "답변1"),
                Pair("질문2", "답변2"),
            )
    }

    "채팅 복사시 좋아요 수와 댓글 수는 복사되지 않는다" {
        val chat = createChat(
            questionAndAnswers = listOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2"),
            ),
            likeCount = 1,
            commentCount = 2
        )

        val result = chat.copy(1L)

        result.likeCount shouldBe 0
        result.commentCount shouldBe 0
    }
})
