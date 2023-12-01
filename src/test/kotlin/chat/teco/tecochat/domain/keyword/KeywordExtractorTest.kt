package chat.teco.tecochat.domain.keyword

import chat.teco.tecochat.createChat
import io.kotest.assertions.extracting
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly

class KeywordExtractorTest : StringSpec({

    "채팅의 키워드를 추출하여 반환한다" {
        val result = KeywordExtractor("#오늘도 #내일도 #행복하길", createChat())

        extracting(result) { keyword }
            .shouldContainExactly("오늘도", "내일도", "행복하길")
    }
})
