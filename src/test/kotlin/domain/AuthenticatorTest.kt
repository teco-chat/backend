package chat.teco.tecochat.auth.domain

import chat.teco.tecochat.createMember
import chat.teco.tecochat.member.domain.MemberRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import java.util.*

class AuthenticatorTest : StringSpec({
    val memberRepository = mockk<MemberRepository>()

    val authenticator = Authenticator(memberRepository)

    "Base64로 인코딩된 닉네임으로 인증한다" {
        val member = createMember()
        every { memberRepository.findByName(any()) } returns Optional.of(member)

        val result = authenticator.authenticateWithBase64("bWFsbGFuZw==")

        result shouldBe member
    }

    "닉네임에 해당하는 회원이 존재하지 않는다면 예외를 던진다" {
        every { memberRepository.findByName(any()) } returns Optional.empty()

        assertThrows<IllegalArgumentException> {
            authenticator.authenticateWithBase64("mallang")
        }
    }
})
