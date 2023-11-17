package chat.teco.tecochat

import chat.teco.tecochat.application.UpdateChatTitleRequest
import chat.teco.tecochat.chat.domain.chat.Chat
import chat.teco.tecochat.chat.domain.chat.GptModel
import chat.teco.tecochat.chat.domain.chat.SettingMessage

const val TITLE = "제목"
const val UPDATED_TITLE = "수정된 제목"
val GPT_MODEL = GptModel.GPT_3_5_TURBO
val SETTING_MESSAGE = SettingMessage.BACK_END_SETTING

fun createChat(
    gptModel: GptModel = GPT_MODEL,
    settingMessage: SettingMessage = SETTING_MESSAGE,
    title: String = TITLE,
    memberId: Long = 1L,
    likeCount: Long = 0L,
    commentCount: Long = 0L,
    id: Long = 0L,
): Chat {
    val chat = Chat(id, gptModel, settingMessage, title, memberId)
    for (i in 1..likeCount) {
        chat.increaseLike()
    }
    for (i in 1..commentCount) {
        chat.increaseComment()
    }
    return chat
}

fun createUpdateChatTitleRequest(
    title: String = UPDATED_TITLE,
): UpdateChatTitleRequest {
    return UpdateChatTitleRequest(title)
}
