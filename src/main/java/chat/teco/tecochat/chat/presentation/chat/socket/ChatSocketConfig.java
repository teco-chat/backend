package chat.teco.tecochat.chat.presentation.chat.socket;

import chat.teco.tecochat.security.WebSocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class ChatSocketConfig implements WebSocketConfigurer {

    private final ChatSocketHandler chatSocketHandler;
    private final WebSocketHandshakeInterceptor webSocketHandShakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatSocketHandler, "/stream/chats/**")
                .addInterceptors(webSocketHandShakeInterceptor)
                .setAllowedOrigins("*");
    }
}
