package chat.teco.tecochat.domain.keyword

import chat.teco.tecochat.createChat
import chat.teco.tecochat.createQuestionAndAnswer
import chat.teco.tecochat.domain.chat.Answer
import chat.teco.tecochat.infra.gpt.GptClient
import io.kotest.assertions.extracting
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.mockk.every
import io.mockk.mockk

class KeywordExtractorTest : StringSpec({

    val gptClient = mockk<GptClient>()

    val keywordExtractor = KeywordExtractor(gptClient)

    "채팅의 키워드를 추출하여 반환한다" {
        val chat = createChat()
        chat.addQuestionAndAnswer(createQuestionAndAnswer())
        every { gptClient.ask(any()) } returns Answer("#오늘도 #내일도 #행복하길")

        val result = keywordExtractor.extractKeywords(chat)

        extracting(result) { keyword }
            .shouldContainExactly("오늘도", "내일도", "행복하길")
    }
})
