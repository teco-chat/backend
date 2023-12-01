package chat.teco.tecochat.infra.gpt

import chat.teco.tecochat.domain.chat.Answer
import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.Question
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

private val EXTRACT_KEYWORD_MESSAGE = """
    Except for this message,
    give just 3 keywords in hashtag format.
    example: #keyword1 #keyword2 #keyword3
""".trimIndent()
private const val FIRST_QNA_INDEX = 0

@Component
class GptClient(
    val restTemplate: RestTemplate,
    val apiKeySettingHeader: HttpHeaders,
    val gptApiUrl: String,
) {
    fun ask(chat: Chat): Answer {
        val request = parseChatCompletionRequest(chat)
        try {
            val response = restTemplate.postForEntity(
                gptApiUrl,
                HttpEntity(request, apiKeySettingHeader),
                ChatCompletionResponse::class.java
            ).body
            return Answer(response.answer())
        } catch (e: Exception) {
            throw IllegalStateException("GPT API에 문제가 있습니다", e)
        }
    }

    private fun parseChatCompletionRequest(chat: Chat): ChatCompletionRequest {
        val questionAndAnswer = chat.questionAndAnswers.questionAndAnswers[FIRST_QNA_INDEX]
        return ChatCompletionRequest.of(
            listOf(
                questionAndAnswer.question,
                questionAndAnswer.answer,
                Question.question(EXTRACT_KEYWORD_MESSAGE)
            )
        )
    }
}
