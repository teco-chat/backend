package chat.teco.tecochat.domain.keyword

import chat.teco.tecochat.domain.chat.Answer
import chat.teco.tecochat.domain.chat.Answer.Companion.answer
import chat.teco.tecochat.domain.chat.GptModel
import chat.teco.tecochat.domain.chat.Message
import com.fasterxml.jackson.annotation.JsonProperty
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

data class ChatCompletionRequest(
    val model: String,
    val messages: List<MessageRequest>,
) {

    companion object {

        @JvmStatic
        fun of(messages: List<Message>): ChatCompletionRequest {
            return messages.stream()
                .map { MessageRequest(it.roleName(), it.content()) }
                .toList().let {
                    ChatCompletionRequest(GptModel.GPT_3_5_TURBO.modelName, it)
                }
        }
    }
}

data class MessageRequest(
    val role: String,
    val content: String,
)

data class UsageResponse(
    @JsonProperty("prompt_tokens") val promptTokens: Int,
    @JsonProperty("completion_tokens") val completionTokens: Int,
    @JsonProperty("total_tokens") val totalTokens: Int,
)

data class MessageResponse(
    @JsonProperty("role") val role: String,
    @JsonProperty("content") val content: String,
)

data class ChoiceResponse(
    @JsonProperty("index") val index: Long,
    @JsonProperty("message") val message: MessageResponse,
    @JsonProperty("finish_reason") val finishReason: String,
)

data class ChatCompletionResponse(
    @JsonProperty("id") val id: String,
    @JsonProperty("object") val `object`: String,
    @JsonProperty("created") val created: Long,
    @JsonProperty("model") val model: String,
    @JsonProperty("choices") val choices: List<ChoiceResponse>,
    @JsonProperty("usage") val usage: UsageResponse,
    @JsonProperty("system_fingerprint") val systemFingerprint: String,
) {
    fun answer(): String {
        return choices[0].message.content
    }
}

