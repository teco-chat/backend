package chat.teco.tecochat.chat.application.chat;

import chat.teco.tecochat.chat.application.chat.dto.ChatSocketContext;
import chat.teco.tecochat.chat.application.chat.mapper.ChatMapper;
import chat.teco.tecochat.chat.domain.chat.event.ChatCreatedEvent;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chat.QuestionAndAnswer;
import com.theokanning.openai.completion.chat.ChatCompletionChunk;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import java.io.IOException;
import java.util.NoSuchElementException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
public class ChatStreamService {

    private final OpenAiService openAiService;
    private final TransactionTemplate transactionTemplate;
    private final ApplicationEventPublisher publisher;
    private final ChatRepository chatRepository;

    public ChatStreamService(OpenAiService openAiService, TransactionTemplate transactionTemplate,
                             ApplicationEventPublisher publisher, ChatRepository chatRepository) {
        this.openAiService = openAiService;
        this.transactionTemplate = transactionTemplate;
        this.publisher = publisher;
        this.chatRepository = chatRepository;
    }

    private static String parseAnswer(ChatCompletionChunk completion) {
        String content = completion.getChoices().get(0).getMessage().getContent();
        if (content == null) {
            return "";
        }
        return content;
    }

    public void streaming(ChatSocketContext context) {
        Chat chat = getOrCreateChat(context);
        ChatCompletionRequest request = ChatMapper.mapToChatCompletionRequest(chat, context.question());
        openAiService.streamChatCompletion(request)
                .blockingForEach(completion -> sendAnswer(context, completion));
        transactionTemplate.executeWithoutResult(status -> finishProcess(chat, context));
    }

    private Chat getOrCreateChat(ChatSocketContext context) {
        if (context.chatId() == null) {
            return Chat.defaultChat(context.member(), context.question());
        }
        return chatRepository.findWithQuestionAndAnswersById(context.chatId())
                .orElseThrow(() -> new NoSuchElementException("채팅이 존재하지 않습니다."));
    }

    private void sendAnswer(ChatSocketContext context, ChatCompletionChunk completion) {
        String answer = parseAnswer(completion);
        sendMessage(context, answer);
        context.stringBuilder().append(answer);
    }

    private void sendMessage(ChatSocketContext context, String message) {
        WebSocketSession session = context.session();
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void finishProcess(Chat chat, ChatSocketContext context) {
        Long chatId = getOrCreateChatId(chat);
        chatRepository.getWithQuestionAndAnswersById(chatId)
                .addQuestionAndAnswer(new QuestionAndAnswer(context.question(), context.answer()));
        sendMessage(context, "[DONE] - ID:" + chatId);
        closeSession(context);
    }

    private Long getOrCreateChatId(Chat chat) {
        if (chat.getId() != 0) {
            return chat.getId();
        }
        Chat save = chatRepository.save(chat);
        publisher.publishEvent(ChatCreatedEvent.from(save));
        return save.getId();
    }

    private void closeSession(ChatSocketContext context) {
        try {
            WebSocketSession session = context.session();
            session.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
