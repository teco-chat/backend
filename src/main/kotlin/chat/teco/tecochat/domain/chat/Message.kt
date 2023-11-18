package chat.teco.tecochat.domain.chat

interface Message {
    fun roleName(): String
    fun content(): String
}
