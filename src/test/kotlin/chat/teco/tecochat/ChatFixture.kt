package chat.teco.tecochat

import chat.teco.tecochat.application.UpdateChatTitleRequest
import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.GptModel
import chat.teco.tecochat.domain.chat.QuestionAndAnswer
import chat.teco.tecochat.domain.chat.SettingMessage

const val TITLE = "제목"
const val UPDATED_TITLE = "수정된 제목"
const val QUESTION = "질문"
const val ANSWER = "답변"
val GPT_MODEL = GptModel.GPT_3_5_TURBO
val SETTING_MESSAGE = SettingMessage.BACK_END_SETTING

fun createChat(
    gptModel: GptModel = GPT_MODEL,
    settingMessage: SettingMessage = SETTING_MESSAGE,
    title: String = TITLE,
    memberId: Long = 1L,
    likeCount: Int = 0,
    commentCount: Int = 0,
    questionAndAnswers: List<QuestionAndAnswer> = listOf(),
    id: Long = 0L,
): Chat {
    val chat = Chat(gptModel, settingMessage, title, memberId, likeCount, commentCount, id = id)
    for (questionAndAnswer in questionAndAnswers) {
        chat.addQuestionAndAnswer(questionAndAnswer)
    }
    return chat
}

fun createQuestionAndAnswer(
    question: String = QUESTION,
    answer: String = ANSWER,
): QuestionAndAnswer {
    return QuestionAndAnswer(question, answer)
}

fun createUpdateChatTitleRequest(
    title: String = UPDATED_TITLE,
): UpdateChatTitleRequest {
    return UpdateChatTitleRequest(title)
}
