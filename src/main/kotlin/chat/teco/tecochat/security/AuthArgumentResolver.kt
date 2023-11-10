package chat.teco.tecochat.security

import chat.teco.tecochat.domain.Authenticator
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

private const val AUTH_HEADER_NAME = "name"

@Component
class AuthArgumentResolver(
    private val authenticator: Authenticator
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(Auth::class.java)
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Long {
        val name = webRequest.getHeader(AUTH_HEADER_NAME)
            ?: throw IllegalStateException("인증에 실패하였습니다.")
        return authenticator.authenticateWithBase64(name).id()
    }
}

