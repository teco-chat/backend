package chat.teco.tecochat.domain.chat

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class QuestionAndAnswerTest : StringSpec({

    "질문과 답변을 복사한다" {
        val questionAndAnswer = QuestionAndAnswer("질문", "답변")

        val result = questionAndAnswer.copy()

        result.question.content() shouldBe "질문"
        result.answer.content() shouldBe "답변"
    }
})
