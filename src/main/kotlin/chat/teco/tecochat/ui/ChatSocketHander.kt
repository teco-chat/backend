package chat.teco.tecochat.ui

import chat.teco.tecochat.application.ChatStreamService
import chat.teco.tecochat.chat.application.chat.dto.ChatSocketContext
import chat.teco.tecochat.domain.member.Member
import lombok.extern.slf4j.Slf4j
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.Objects

private const val DEFAULT_CHAT_SOCKET_URI = "/stream/chats"
private const val AUTH_SESSION_NAME = "auth"

@Slf4j
@Component
class ChatSocketHandler(
    private val chatStreamService: ChatStreamService,
) : TextWebSocketHandler() {

    private val socketContextMap: MutableMap<String, ChatSocketContext> = HashMap()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        val member = session.attributes[AUTH_SESSION_NAME] as? Member
        val chatId = parseChatId(session)
        socketContextMap[session.id] = ChatSocketContext(session, member, chatId)
    }

    private fun parseChatId(session: WebSocketSession): Long? {
        return Objects.requireNonNull(session.uri).getPath()
            .substring(DEFAULT_CHAT_SOCKET_URI.length)
            .replace("/", "")
            .toLongOrNull()
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        socketContextMap.remove(session.id)
    }

    public override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val context = socketContextMap[session.id]!!
        context.setQuestion(message.payload)
        chatStreamService.streaming(context)
    }
}
