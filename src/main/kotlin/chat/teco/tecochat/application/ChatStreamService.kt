package chat.teco.tecochat.application

import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.Chat.Companion.defaultChat
import chat.teco.tecochat.domain.chat.ChatCreatedEvent.Companion.from
import chat.teco.tecochat.domain.chat.ChatRepository
import chat.teco.tecochat.domain.chat.Question
import chat.teco.tecochat.domain.chat.QuestionAndAnswer
import chat.teco.tecochat.domain.chat.getWithQuestionAndAnswersByIdOrThrow
import chat.teco.tecochat.infra.gpt.ChatSocketContext
import com.theokanning.openai.completion.chat.ChatCompletionChunk
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.service.OpenAiService
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.springframework.transaction.support.TransactionTemplate

@Component
class ChatStreamService(
    private val openAiService: OpenAiService,
    private val transactionTemplate: TransactionTemplate,
    private val publisher: ApplicationEventPublisher,
    private val chatRepository: ChatRepository,
) {

    fun streaming(context: ChatSocketContext) {
        val chat: Chat = getOrCreateChat(context)
        val qnas = chat.last3QuestionAndAnswers()
        val messages = qnas.messagesWithSettingMessage(chat.settingMessage)
        messages.add(Question.question(context.getCurrentQuestion()))
        val chatMessages = messages.map { ChatMessage(it.roleName(), it.content()) }
        val request = ChatCompletionRequest.builder()
            .model(chat.modelName())
            .messages(chatMessages)
            .build()
        openAiService.streamChatCompletion(request)
            .blockingForEach { completion -> sendAnswer(context, completion) }
        transactionTemplate.executeWithoutResult { status -> finishProcess(chat, context) }
    }

    private fun getOrCreateChat(context: ChatSocketContext): Chat {
        if (context.chatId == null) {
            return defaultChat(context.member, context.getCurrentQuestion())
        }
        return chatRepository.getWithQuestionAndAnswersByIdOrThrow(context.chatId)
    }

    private fun sendAnswer(context: ChatSocketContext, completion: ChatCompletionChunk) {
        val answer = completion.choices[0].message.content ?: ""
        context.sendMessage(answer)
        context.addAnswer(answer)
    }

    private fun finishProcess(chat: Chat, context: ChatSocketContext) {
        val chatId = getOrCreateChatId(chat)
        chatRepository.getWithQuestionAndAnswersByIdOrThrow(chatId)
            .addQuestionAndAnswer(QuestionAndAnswer(context.getCurrentQuestion(), context.getAnswer()))
        context.sendMessage("[DONE] - ID:$chatId")
        context.close()
    }

    private fun getOrCreateChatId(chat: Chat): Long {
        if (chat.id != 0L) {
            return chat.id
        }
        val save = chatRepository.save(chat)
        publisher.publishEvent(from(save))
        return save.id
    }
}
