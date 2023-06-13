package chat.teco.tecochat.auth.presentation.socket;

import chat.teco.tecochat.member.domain.Member;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthHandShakeInterceptor extends HttpSessionHandshakeInterceptor {

    public static final String AUTH_SESSION_NAME = "auth";

    private final SocketAuthenticator socketAuthenticator;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {
        if (!super.beforeHandshake(request, response, wsHandler, attributes)) {
            return false;
        }
        Member member = socketAuthenticator.authenticate(request);
        attributes.put(AUTH_SESSION_NAME, member);
        return true;
    }
}
