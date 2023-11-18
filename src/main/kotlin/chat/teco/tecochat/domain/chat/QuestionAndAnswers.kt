package chat.teco.tecochat.domain.chat

import jakarta.persistence.CascadeType
import jakarta.persistence.Embeddable
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany

@Embeddable
class QuestionAndAnswers(
    @OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    val questionAndAnswers: MutableList<QuestionAndAnswer> = ArrayList(),
) {

    fun add(questionAndAnswer: QuestionAndAnswer) {
        questionAndAnswers.add(questionAndAnswer)
    }

    fun last3QuestionAndAnswers(): QuestionAndAnswers {
        val size = questionAndAnswers.size
        return if (size < 3) {
            this
        } else QuestionAndAnswers(questionAndAnswers.subList(size - 3, size))
    }

    fun messagesWithSettingMessage(settingMessage: SettingMessage): List<Message> {
        val result: MutableList<Message> = ArrayList()
        result.add(settingMessage)
        for (qna in questionAndAnswers) {
            result.add(qna.question)
            result.add(qna.answer)
        }
        return result
    }

    fun questionAndAnswers(): List<QuestionAndAnswer> {
        return ArrayList(questionAndAnswers)
    }
}
