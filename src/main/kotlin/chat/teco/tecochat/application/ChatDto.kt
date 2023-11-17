package chat.teco.tecochat.application

import com.fasterxml.jackson.annotation.JsonCreator

data class UpdateChatTitleRequest @JsonCreator constructor(
    val title: String,
)

