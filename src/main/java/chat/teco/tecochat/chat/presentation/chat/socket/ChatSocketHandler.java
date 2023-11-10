package chat.teco.tecochat.chat.presentation.chat.socket;

import static java.util.Objects.requireNonNull;

import chat.teco.tecochat.chat.application.chat.ChatStreamService;
import chat.teco.tecochat.chat.application.chat.dto.ChatSocketContext;
import chat.teco.tecochat.member.domain.Member;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class ChatSocketHandler extends TextWebSocketHandler {

    private static final String DEFAULT_CHAT_SOCKET_URI = "/stream/chats";
    private static final String AUTH_SESSION_NAME = "auth";

    private final ChatStreamService chatStreamService;
    private final Map<String, ChatSocketContext> socketContextMap = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Member member = (Member) session.getAttributes().get(AUTH_SESSION_NAME);
        socketContextMap.put(session.getId(), new ChatSocketContext(session, member, parseChatId(session)));
    }

    private Long parseChatId(WebSocketSession session) {
        String path = requireNonNull(session.getUri()).getPath();
        try {
            return Long.parseLong(path.substring(DEFAULT_CHAT_SOCKET_URI.length())
                    .replace("/", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        socketContextMap.remove(session.getId());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        ChatSocketContext context = socketContextMap.get(session.getId());
        context.setQuestion(message.getPayload());
        chatStreamService.streaming(context);
    }
}
