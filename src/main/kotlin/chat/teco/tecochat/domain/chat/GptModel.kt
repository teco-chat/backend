package chat.teco.tecochat.domain.chat

enum class GptModel(
    val modelName: String,
    val maxTokens: Int,
) {
    GPT_3_5_TURBO("gpt-3.5-turbo-1106", 4096),
    GPT_4("gpt-4", 8192),
    GPT_4_32K("gpt-4-32k", 32768),
}
