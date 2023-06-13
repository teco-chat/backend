package chat.teco.tecochat.auth.presentation.socket;

import chat.teco.tecochat.auth.domain.Authenticator;
import chat.teco.tecochat.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SocketAuthenticator {

    private static final String AUTH_QUERY_STRING_NAME = "name=";

    private final Authenticator authenticator;

    public Member authenticate(ServerHttpRequest request) {
        String name = getName(request);
        return authenticator.authenticateWithBase64(name);
    }

    private String getName(ServerHttpRequest request) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String queryString = servletRequest.getQueryString();
        return queryString.replace(AUTH_QUERY_STRING_NAME, "")
                .strip();
    }
}
