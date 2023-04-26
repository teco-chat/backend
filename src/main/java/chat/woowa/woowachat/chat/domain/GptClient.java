package chat.woowa.woowachat.chat.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class GptClient {

    private static final String URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final HttpHeaders apiKeySettingHeader;

    public GptClient(final RestTemplate restTemplate, final HttpHeaders apiKeySettingHeader) {
        this.restTemplate = restTemplate;
        this.apiKeySettingHeader = apiKeySettingHeader;
    }

    public Message ask(final Chat chat) {
        final ChatCompletionRequest request = ChatCompletionRequest.from(chat);
        final ChatCompletionResponse response = restTemplate.postForEntity(
                        URL,
                        new HttpEntity<>(request, apiKeySettingHeader),
                        ChatCompletionResponse.class)
                .getBody();
        return Objects.requireNonNull(response).answer();
    }

    public record ChatCompletionRequest(
            String model,
            List<MessageRequest> messages
    ) {
        public static ChatCompletionRequest from(final Chat chat) {
            final List<MessageRequest> messageRequests = new ArrayList<>();
            final List<Message> messages = chat.messagesWithFreeToken();
            for (final Message message : messages) {
                messageRequests.add(new MessageRequest(
                        message.roleName(), message.content()));
            }
            return new ChatCompletionRequest(chat.modelName(), messageRequests);
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
        public Message answer() {
            return Message.assistant(choices.get(0).message.content, usage.completionTokens);
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
