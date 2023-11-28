package chat.teco.tecochat.config

import chat.teco.tecochat.security.WebSocketHandshakeInterceptor
import chat.teco.tecochat.ui.ChatSocketHandler
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@EnableWebSocket
@Configuration
class ChatSocketConfig(
    private val chatSocketHandler: ChatSocketHandler,
    private val webSocketHandShakeInterceptor: WebSocketHandshakeInterceptor,
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(chatSocketHandler, "/stream/chats/**")
            .addInterceptors(webSocketHandShakeInterceptor)
            .setAllowedOrigins("*")
    }
}

