package chat.teco.tecochat.domain.keyword

import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.Question
import org.springframework.stereotype.Component

private const val HASHTAG_INDEX = 1
private const val VALID_KEYWORD_COUNT = 3
private val EXTRACT_KEYWORD_MESSAGE = """
    Except for this message,
    give just 3 keywords in hashtag format.
    example: #keyword1 #keyword2 #keyword3
""".trimIndent()

@Component
class KeywordExtractor(
    private val gptClient: GptClient,
) {

    fun extractKeywords(chat: Chat): List<Keyword> {
        val questionAndAnswer = chat.questionAndAnswers.questionAndAnswers[0]
        val answer = gptClient.ask(
            listOf(
                questionAndAnswer.question,
                questionAndAnswer.answer,
                Question.question(EXTRACT_KEYWORD_MESSAGE)
            )
        )
        return answer.content().split(" ")
            .map { it.trim() }
            .map { it.substring(HASHTAG_INDEX) }
            .map { Keyword(it, chat) }
            .take(VALID_KEYWORD_COUNT)
    }
}
