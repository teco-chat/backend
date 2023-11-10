package chat.teco.tecochat.security

import chat.teco.tecochat.domain.Authenticator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.MethodParameter
import org.springframework.web.context.request.NativeWebRequest

class AuthArgumentResolverTest : StringSpec({
    val authenticator = mockk<Authenticator>()
    val methodParameter = mockk<MethodParameter>()
    val nativeWebRequest = mockk<NativeWebRequest>()

    val authArgumentResolver = AuthArgumentResolver(authenticator)

    "인증을 위한 값이 없는 경우 예외가 발생한다" {
        every { nativeWebRequest.getHeader("name") } returns null

        shouldThrow<IllegalStateException> {
            authArgumentResolver.resolveArgument(methodParameter, null, nativeWebRequest, null)
        }
    }
})
