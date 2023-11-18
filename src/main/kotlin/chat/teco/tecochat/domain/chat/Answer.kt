package chat.teco.tecochat.domain.chat

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Answer(
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    val answer: String,
) : Message {

    override fun roleName(): String {
        return Role.ASSISTANT.roleName
    }

    override fun content(): String {
        return answer
    }

    companion object {
        @JvmStatic
        fun answer(content: String): Answer {
            return Answer(content)
        }
    }
}
