package chat.teco.tecochat.chat.application.chat.mapper;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.Message;
import chat.teco.tecochat.chat.domain.chat.Question;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswers;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import java.util.List;

public class ChatMapper {

    public static ChatCompletionRequest mapToChatCompletionRequest(Chat chat, String question) {
        QuestionAndAnswers qnas = chat.last3QuestionAndAnswers();
        List<Message> messages = qnas.messagesWithSettingMessage(chat.settingMessage());
        messages.add(Question.question(question));
        List<ChatMessage> chatMessages = messages.stream()
                .map(it -> new ChatMessage(it.roleName(), it.content()))
                .toList();
        return ChatCompletionRequest.builder()
                .model(chat.modelName())
                .messages(chatMessages)
                .build();
    }
}
