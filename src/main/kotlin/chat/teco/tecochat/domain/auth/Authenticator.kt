package chat.teco.tecochat.domain.auth

import chat.teco.tecochat.common.util.Base64Util
import chat.teco.tecochat.member.domain.Member
import chat.teco.tecochat.member.domain.MemberRepository
import org.springframework.stereotype.Component

@Component
class Authenticator(
    private val memberRepository: MemberRepository
) {

    fun authenticateWithBase64(encodedName: String): Member {
        return memberRepository.findByName(Base64Util.decodeBase64(encodedName))
            .orElseThrow { IllegalArgumentException("인증에 실패했습니다.") }
    }
}
