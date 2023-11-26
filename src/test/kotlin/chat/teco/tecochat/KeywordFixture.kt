package chat.teco.tecochat

import chat.teco.tecochat.infra.gpt.ChatCompletionResponse
import chat.teco.tecochat.infra.gpt.ChoiceResponse
import chat.teco.tecochat.infra.gpt.MessageResponse
import chat.teco.tecochat.infra.gpt.UsageResponse

const val ASSISTANT_CONTENT = "답변"

fun createChatCompletionResponse(
    content: String = ASSISTANT_CONTENT,
): ChatCompletionResponse {
    val response = ChoiceResponse(
        1L,
        MessageResponse("assistant", content),
        "stop"
    )
    return ChatCompletionResponse(
        "",
        "",
        0L,
        "",
        listOf(response),
        UsageResponse(1500, 500, 2000),
        ""
    )
}
