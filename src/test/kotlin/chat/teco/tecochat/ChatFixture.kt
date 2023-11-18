package chat.teco.tecochat

import chat.teco.tecochat.application.UpdateChatTitleRequest
import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.GptModel
import chat.teco.tecochat.domain.chat.SettingMessage

const val TITLE = "제목"
const val UPDATED_TITLE = "수정된 제목"
val GPT_MODEL = GptModel.GPT_3_5_TURBO
val SETTING_MESSAGE = SettingMessage.BACK_END_SETTING

fun createChat(
    gptModel: GptModel = GPT_MODEL,
    settingMessage: SettingMessage = SETTING_MESSAGE,
    title: String = TITLE,
    memberId: Long = 1L,
    likeCount: Int = 0,
    commentCount: Int = 0,
    id: Long = 0L,
): Chat {
    return Chat(gptModel, settingMessage, title, memberId, likeCount, commentCount, id = id)
}

fun createUpdateChatTitleRequest(
    title: String = UPDATED_TITLE,
): UpdateChatTitleRequest {
    return UpdateChatTitleRequest(title)
}
