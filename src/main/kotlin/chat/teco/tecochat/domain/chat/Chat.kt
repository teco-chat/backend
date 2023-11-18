package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.domain.member.Member
import chat.teco.tecochat.support.domain.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class Chat(

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val model: GptModel,

    @Enumerated(EnumType.STRING)
    val settingMessage: SettingMessage,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false)
    val memberId: Long,

    var likeCount: Int = 0,
    var commentCount: Int = 0,

    @Embedded
    val questionAndAnswers: QuestionAndAnswers = QuestionAndAnswers(),

    id: Long = 0L,
) : BaseEntity(id) {

    constructor(gptModel: GptModel, settingMessage: SettingMessage, title: String, memberId: Long) : this(
        model = gptModel,
        settingMessage = settingMessage,
        title = title,
        memberId = memberId
    )

    constructor(id: Long, gptModel: GptModel, settingMessage: SettingMessage, title: String, memberId: Long) : this(
        model = gptModel,
        settingMessage = settingMessage,
        title = title,
        memberId = memberId,
        id = id
    )

    fun addQuestionAndAnswer(questionAndAnswer: QuestionAndAnswer) {
        questionAndAnswers.add(questionAndAnswer)
    }

    fun last3QuestionAndAnswers(): QuestionAndAnswers {
        return questionAndAnswers.last3QuestionAndAnswers()
    }

    fun decreaseLike() {
        likeCount--
    }

    fun increaseLike() {
        likeCount++
    }

    fun decreaseComment() {
        commentCount--
    }

    fun increaseComment() {
        commentCount++
    }

    fun updateTitle(memberId: Long, title: String) {
        check(this.memberId == memberId) { "제목을 수정할 권한이 없습니다." }
        this.title = title
    }

    fun copy(memberId: Long): Chat {
        val copied = Chat(model, settingMessage, title, memberId)
        for (questionAndAnswer in questionAndAnswers.questionAndAnswers) {
            copied.addQuestionAndAnswer(questionAndAnswer.copy())
        }
        return copied
    }

    fun modelName(): String {
        return model.modelName
    }

    companion object {
        @JvmStatic
        fun defaultChat(member: Member, title: String): Chat {
            return Chat(
                GptModel.GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course),
                title,
                member.id
            )
        }
    }
}
