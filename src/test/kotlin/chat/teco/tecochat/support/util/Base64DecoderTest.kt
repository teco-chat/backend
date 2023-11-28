package chat.teco.tecochat.support.util

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class Base64DecoderTest : StringSpec({

    "Base64로 인코딩된 문자열을 디코딩한다" {
        Base64Decoder("7ZeI67iM") shouldBe "허브"
    }
})
