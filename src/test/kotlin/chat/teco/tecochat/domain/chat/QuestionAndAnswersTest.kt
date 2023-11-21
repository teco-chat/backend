package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.createQuestionAndAnswer
import io.kotest.assertions.extracting
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe

class QuestionAndAnswersTest : StringSpec({

    "질문과 답변을 추가한다" {
        val questionAndAnswers = QuestionAndAnswers()

        questionAndAnswers.add(createQuestionAndAnswer())

        questionAndAnswers.questionAndAnswers.size shouldBe 1
    }

    "마지막 3개의 질문과 답변을 반환한다" {
        val questionAndAnswers = QuestionAndAnswers(
            mutableListOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2"),
                createQuestionAndAnswer("질문3", "답변3"),
                createQuestionAndAnswer("질문4", "답변4")
            )
        )

        val result = questionAndAnswers.last3QuestionAndAnswers()

        extracting(result.questionAndAnswers) { Pair(question.question, answer.answer) }
            .shouldContainExactly(
                Pair("질문2", "답변2"),
                Pair("질문3", "답변3"),
                Pair("질문4", "답변4"),
            )
    }

    "질문과 답변이 3개 보다 적다면 전부 반환한다" {
        val questionAndAnswers = QuestionAndAnswers(
            mutableListOf(
                createQuestionAndAnswer("질문1", "답변1"),
                createQuestionAndAnswer("질문2", "답변2"),
            )
        )

        val result = questionAndAnswers.last3QuestionAndAnswers()

        extracting(result.questionAndAnswers) { Pair(question.question, answer.answer) }
            .shouldContainExactly(
                Pair("질문1", "답변1"),
                Pair("질문2", "답변2"),
            )
    }
})
