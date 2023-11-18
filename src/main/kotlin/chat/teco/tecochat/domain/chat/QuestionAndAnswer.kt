package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.support.domain.BaseEntity
import jakarta.persistence.Embedded
import jakarta.persistence.Entity

@Entity
class QuestionAndAnswer(

    @Embedded
    val question: Question,

    @Embedded
    val answer: Answer,

    id: Long = 0L,
) : BaseEntity(id) {

    constructor(question: String, answer: String) : this(
        Question.question(question), Answer.answer(answer)
    )

    fun copy(): QuestionAndAnswer {
        return QuestionAndAnswer(question, answer)
    }
}
