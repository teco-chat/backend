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

    // TODO 추후에 resources에 넣어줘
    private static final String URL = "https://api.openai.com/v1/chat/completions";

    private final RestTemplate restTemplate;
    private final HttpHeaders apiKeySettingHeader;

    public GptClient(final RestTemplate restTemplate, final HttpHeaders apiKeySettingHeader) {
        this.restTemplate = restTemplate;
        this.apiKeySettingHeader = apiKeySettingHeader;
    }

    public QuestionAndAnswer ask(final Chat chat, final Question question) {
        final ChatCompletionRequest request = ChatCompletionRequest.of(chat, question);
        try {
            final ChatCompletionResponse response = restTemplate.postForEntity(
                            URL,
                            new HttpEntity<>(request, apiKeySettingHeader),
                            ChatCompletionResponse.class)
                    .getBody();
            Objects.requireNonNull(response);
            return new QuestionAndAnswer(
                    question,
                    Answer.answer(response.choices().get(0).message.content),
                    (response.usage().totalTokens - chat.totalToken())
            );
        } catch (final Exception e) {
            // TODO 적절히 예외처리
            if (e.getMessage().contains("context_length_exceeded")) {
                throw new IllegalArgumentException("질문이 너무 깁니다. 질문의 크기를 줄여주세요");
            }
            throw new RuntimeException("GPT API 에 문제가 있습니다", e);
        }
    }

    public record ChatCompletionRequest(
            String model,
            List<MessageRequest> messages
    ) {
        public static ChatCompletionRequest of(final Chat chat, final Question question) {
            final List<MessageRequest> messageRequests = new ArrayList<>();
            final List<Message> messages = chat.messagesWithFreeToken();
            messages.add(question);
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
