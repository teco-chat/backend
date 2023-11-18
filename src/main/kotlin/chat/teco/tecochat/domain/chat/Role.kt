package chat.teco.tecochat.domain.chat

enum class Role(
    val roleName: String,
) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant")
}
