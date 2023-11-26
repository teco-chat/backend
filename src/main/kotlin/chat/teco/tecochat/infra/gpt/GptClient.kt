package chat.teco.tecochat.infra.gpt

import chat.teco.tecochat.domain.chat.Answer
import chat.teco.tecochat.domain.chat.Answer.Companion.answer
import chat.teco.tecochat.domain.chat.Message
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.Objects

@Component
class GptClient(
    val restTemplate: RestTemplate,
    val apiKeySettingHeader: HttpHeaders,
    val gptApiUrl: String,
) {
    fun ask(messages: List<Message>): Answer {
        val request = ChatCompletionRequest.of(messages)

        try {
            val response = restTemplate.postForEntity(
                gptApiUrl,
                HttpEntity(request, apiKeySettingHeader),
                ChatCompletionResponse::class.java
            ).body
            Objects.requireNonNull(response)
            return answer(response.answer())
        } catch (e: Exception) {
            throw IllegalStateException("GPT API에 문제가 있습니다", e)
        }
    }
}
