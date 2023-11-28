package chat.teco.tecochat.infra.gpt

import chat.teco.tecochat.domain.member.Member
import org.springframework.web.socket.WebSocketSession

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

    fun getAnswer() = answers.joinToString("")

    fun getCurrentQuestion() = question
}
