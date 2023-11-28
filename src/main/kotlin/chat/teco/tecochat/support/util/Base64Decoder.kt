package chat.teco.tecochat.support.util

import java.util.Base64

object Base64Decoder {

    operator fun invoke(encodedString: String): String {
        val names = Base64.getDecoder()
            .decode(encodedString)
        return String(names).intern()
    }
}

