package chat.teco.tecochat.infra.gpt

import chat.teco.tecochat.domain.member.Member
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException

class ChatSocketContext(
    val session: WebSocketSession,
    val member: Member,
    val chatId: Long?,
) {
    private val answers: MutableList<String> = mutableListOf()
    private var question: String = ""

    fun addAnswer(answer: String) {
        answers.add(answer)
    }

    fun changeQuestion(question: String) {
        this.question = question
    }

    fun sendMessage(message: String) {
        try {
            session.sendMessage(TextMessage(message))
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun close() {
        try {
            session.close()
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun getAnswer() = answers.joinToString("")

    fun getCurrentQuestion() = question
}
