package chat.teco.tecochat.application

import com.fasterxml.jackson.annotation.JsonCreator

data class ChatLikeRequest @JsonCreator constructor(
    val chatId: Long
)
