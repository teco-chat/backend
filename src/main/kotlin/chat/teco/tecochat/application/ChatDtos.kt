package chat.teco.tecochat.application

import com.fasterxml.jackson.annotation.JsonCreator

data class UpdateChatTitleRequest @JsonCreator constructor(
    val title: String,
)

data class CopyChatResponse @JsonCreator constructor(
    val copiedChatId: Long,
)
