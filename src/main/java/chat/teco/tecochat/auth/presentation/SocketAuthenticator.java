package chat.teco.tecochat.auth.presentation;

import static chat.teco.tecochat.auth.exception.AuthenticationExceptionType.NO_NAME_HEADER;

import chat.teco.tecochat.auth.domain.Authenticator;
import chat.teco.tecochat.auth.exception.AuthenticationException;
import chat.teco.tecochat.member.domain.Member;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@RequiredArgsConstructor
@Component
public class SocketAuthenticator {

    private static final String AUTH_HEADER_NAME = "name";

    private final Authenticator authenticator;

    public Member authenticate(WebSocketSession session) {
        String name = getName(session.getHandshakeHeaders());
        return authenticator.authenticateWithBase64(name);
    }

    private String getName(HttpHeaders httpHeaders) {
        List<String> nameHeader = httpHeaders.get(AUTH_HEADER_NAME);
        if (nameHeader == null || nameHeader.isEmpty()) {
            throw new AuthenticationException(NO_NAME_HEADER);
        }
        return nameHeader.get(0);
    }
}
