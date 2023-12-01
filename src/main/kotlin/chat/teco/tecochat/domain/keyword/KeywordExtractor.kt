package chat.teco.tecochat.domain.keyword

import chat.teco.tecochat.domain.chat.Chat

private const val HASHTAG_INDEX = 1
private const val VALID_KEYWORD_COUNT = 3

object KeywordExtractor {

    operator fun invoke(answer: String, chat: Chat): List<Keyword> {
        return answer.split(" ")
            .map { it.trim() }
            .map { it.substring(HASHTAG_INDEX) }
            .map { Keyword(it, chat) }
            .take(VALID_KEYWORD_COUNT)
    }
}
