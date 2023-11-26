package chat.teco.tecochat.infra.gpt

import chat.teco.tecochat.createChatCompletionResponse
import chat.teco.tecochat.domain.chat.Answer.Companion.answer
import chat.teco.tecochat.domain.chat.Question.Companion.question
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

class GptClientTest : StringSpec({

    val restTemplate = mockk<RestTemplate>()

    val client = GptClient(restTemplate, HttpHeaders(), "")

    "질문에 대한 응답을 반환한다" {
        val content = "답변"
        val messages = listOf(question("Q1"), answer("A1"))
        every { restTemplate.postForEntity(any<String>(), any(), any<Class<*>>()) } returns
                ResponseEntity.status(200).body(createChatCompletionResponse(content = content))

        val result = client.ask(messages)

        result.content() shouldBe content
    }

    "GPT API에 문제가 있는 경우 예외를 던진다" {
        val messages = listOf(question("Q1"), answer("A1"))
        every { restTemplate.postForEntity(any<String>(), any(), any<Class<*>>()) } throws
                RestClientException("some problem")

        shouldThrow<IllegalStateException> { client.ask(messages) }
    }
})
