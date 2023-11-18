package chat.teco.tecochat.domain.chat

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Question(
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    val question: String,
) : Message {

    override fun roleName(): String {
        return Role.USER.roleName
    }

    override fun content(): String {
        return question
    }

    companion object {
        @JvmStatic
        fun question(content: String): Question {
            return Question(content)
        }
    }
}
