package chat.teco.tecochat.domain.auth

import chat.teco.tecochat.domain.member.Member
import chat.teco.tecochat.domain.member.MemberRepository
import chat.teco.tecochat.support.util.Base64Decoder
import org.springframework.stereotype.Component

@Component
class Authenticator(
    private val memberRepository: MemberRepository,
) {

    fun authenticateWithBase64(encodedName: String): Member {
        return memberRepository.findByName(Base64Decoder(encodedName))
            ?: throw IllegalArgumentException("인증에 실패했습니다.")
    }
}
