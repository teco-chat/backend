package chat.teco.tecochat.chat.domain.keyword;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.GPT_API_ERROR;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.QUESTION_SIZE_TOO_BIG;
import static java.util.Objects.requireNonNull;

import chat.teco.tecochat.chat.domain.chat.Answer;
import chat.teco.tecochat.chat.domain.chat.Message;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GptClient {

    private final RestTemplate restTemplate;
    private final HttpHeaders apiKeySettingHeader;
    private final String gptApiUrl;

    public GptClient(
            RestTemplate restTemplate,
            HttpHeaders apiKeySettingHeader,
            String gptApiUrl
    ) {
        this.restTemplate = restTemplate;
        this.apiKeySettingHeader = apiKeySettingHeader;
        this.gptApiUrl = gptApiUrl;
    }

    public Answer ask(List<Message> messages) {
        ChatCompletionRequest request = ChatCompletionRequest.of(messages);
        try {
            ChatCompletionResponse response = restTemplate.postForEntity(gptApiUrl,
                    new HttpEntity<>(request, apiKeySettingHeader),
                    ChatCompletionResponse.class
            ).getBody();
            requireNonNull(response);
            return Answer.answer(response.answer());
        } catch (Exception e) {
            if (e.getMessage().contains("context_length_exceeded")) {
                throw new ChatException(QUESTION_SIZE_TOO_BIG);
            }
            throw new ChatException(GPT_API_ERROR);
        }
    }

    public record ChatCompletionRequest(
            String model,
            List<MessageRequest> messages
    ) {
        public static ChatCompletionRequest of(List<Message> messages) {
            List<MessageRequest> messageRequests = new ArrayList<>();
            for (Message message : messages) {
                messageRequests.add(new MessageRequest(message.roleName(), message.content()));
            }
            return new ChatCompletionRequest(GPT_3_5_TURBO.modelName(), messageRequests);
        }

        public record MessageRequest(
                String role,
                String content
        ) {
        }
    }

    public record ChatCompletionResponse(
            String id,
            String object,
            String created,
            List<ChoiceResponse> choices,
            UsageResponse usage
    ) {
        public String answer() {
            return choices.get(0).message().content();
        }

        public record ChoiceResponse(
                Long index,
                MessageResponse message,
                @JsonProperty("finish_reason")
                String finishReason
        ) {
        }

        public record MessageResponse(
                String role,
                String content
        ) {
        }

        public record UsageResponse(
                @JsonProperty("prompt_tokens")
                int promptTokens,
                @JsonProperty("completion_tokens")
                int completionTokens,
                @JsonProperty("total_tokens")
                int totalTokens
        ) {
        }
    }
}
