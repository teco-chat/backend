package chat.teco.tecochat.application

import chat.teco.tecochat.chat.application.chat.dto.ChatSocketContext
import chat.teco.tecochat.chat.application.chat.mapper.ChatMapper
import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.Chat.Companion.defaultChat
import chat.teco.tecochat.domain.chat.ChatCreatedEvent.Companion.from
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.QuestionAndAnswer
import com.theokanning.openai.completion.chat.ChatCompletionChunk
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.service.OpenAiService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.socket.TextMessage
import java.io.IOException

@Component
class ChatStreamService(
    private val openAiService: OpenAiService,
    private val transactionTemplate: TransactionTemplate,
    private val publisher: ApplicationEventPublisher,
    private val chatRepository: ChatRepository,
) {

    fun streaming(context: ChatSocketContext) {
        val chat: Chat = getOrCreateChat(context)
        val request: ChatCompletionRequest = ChatMapper.mapToChatCompletionRequest(chat, context.question())
        openAiService.streamChatCompletion(request)
            .blockingForEach { completion -> sendAnswer(context, completion) }
        transactionTemplate.executeWithoutResult { status -> finishProcess(chat, context) }
    }

    private fun getOrCreateChat(context: ChatSocketContext): Chat {
        return if (context.chatId() == null) {
            defaultChat(context.member(), context.question())
        } else chatRepository.findWithQuestionAndAnswersById(context.chatId())
            .orElseThrow { NoSuchElementException("채팅이 존재하지 않습니다.") }
    }

    private fun sendAnswer(context: ChatSocketContext, completion: ChatCompletionChunk) {
        val answer = completion.choices[0].message.content ?: ""
        sendMessage(context, answer)
        context.stringBuilder().append(answer)
    }

    private fun sendMessage(context: ChatSocketContext, message: String) {
        val session = context.session()
        try {
            session.sendMessage(TextMessage(message))
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun finishProcess(chat: Chat, context: ChatSocketContext) {
        val chatId = getOrCreateChatId(chat)
        chatRepository.getWithQuestionAndAnswersById(chatId)
            .addQuestionAndAnswer(QuestionAndAnswer(context.question(), context.answer()))
        sendMessage(context, "[DONE] - ID:$chatId")
        closeSession(context)
    }

    private fun getOrCreateChatId(chat: Chat): Long {
        if (chat.id != 0L) {
            return chat.id
        }
        val save = chatRepository.save(chat)
        publisher.publishEvent(from(save))
        return save.id
    }

    private fun closeSession(context: ChatSocketContext) {
        try {
            val session = context.session()
            session.close()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
