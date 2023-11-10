package chat.teco.tecochat.security

import chat.teco.tecochat.domain.Authenticator
import chat.teco.tecochat.member.domain.Member
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component

private const val AUTH_QUERY_STRING_NAME = "name="

@Component
class SocketAuthenticator(
    private val authenticator: Authenticator
) {

    fun authenticate(request: ServerHttpRequest): Member {
        val name = parseEncodedName(request)
        return authenticator.authenticateWithBase64(name)
    }

    private fun parseEncodedName(request: ServerHttpRequest): String {
        val servletRequest = (request as ServletServerHttpRequest).servletRequest
        val queryString = servletRequest.queryString
        return queryString.replace(AUTH_QUERY_STRING_NAME, "")
    }
}


