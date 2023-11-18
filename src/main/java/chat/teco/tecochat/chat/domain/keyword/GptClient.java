package chat.teco.tecochat.chat.domain.keyword;

import static chat.teco.tecochat.domain.chat.GptModel.GPT_3_5_TURBO;
import static java.util.Objects.requireNonNull;

import chat.teco.tecochat.domain.chat.Answer;
import chat.teco.tecochat.domain.chat.Message;
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
            return Answer.Companion.answer(response.answer());
        } catch (Exception e) {
            if (e.getMessage().contains("context_length_exceeded")) {
                throw new IllegalArgumentException("질문하신 내용의 길이가 너무 깁니다. 질문의 길이를 줄여주세요.");
            }
            throw new IllegalStateException("GPT API 에 문제가 있습니다");
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
            return new ChatCompletionRequest(GPT_3_5_TURBO.getModelName(), messageRequests);
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
