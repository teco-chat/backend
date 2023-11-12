package chat.teco.tecochat.security

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor

private const val AUTH_SESSION_NAME = "auth"

@Component
class WebSocketHandshakeInterceptor(
    private val socketAuthenticator: SocketAuthenticator
) : HttpSessionHandshakeInterceptor() {

    @Throws(Exception::class)
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        if (!super.beforeHandshake(request, response, wsHandler, attributes)) {
            return false
        }

        val member = socketAuthenticator.authenticate(request)
        attributes[AUTH_SESSION_NAME] = member
        return true
    }
}
