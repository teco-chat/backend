package chat.teco.tecochat.chat.domain.keyword;

import static chat.teco.tecochat.chat.domain.chat.Answer.answer;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.domain.chat.Answer;
import chat.teco.tecochat.chat.domain.chat.Message;
import chat.teco.tecochat.chat.domain.keyword.GptClient.ChatCompletionResponse;
import chat.teco.tecochat.chat.domain.keyword.GptClient.ChatCompletionResponse.ChoiceResponse;
import chat.teco.tecochat.chat.domain.keyword.GptClient.ChatCompletionResponse.MessageResponse;
import chat.teco.tecochat.chat.domain.keyword.GptClient.ChatCompletionResponse.UsageResponse;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("GptClient 은(는)")
class GptClientTest {

    private static final String OVER_MAX_TOKEN_CODE = "context_length_exceeded";

    private final HttpHeaders apiKeySettingHeader = new HttpHeaders();
    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final GptClient client = new GptClient(restTemplate, apiKeySettingHeader, "");

    @Test
    void 질문에_대해_응답_반환() {
        // given
        List<Message> messages = List.of(question("Q1"), answer("A1"));
        ChatCompletionResponse response = new ChatCompletionResponse("", "", "",
                List.of(new ChoiceResponse(1L,
                        new MessageResponse("assistant", "답변"),
                        "stop")),
                new UsageResponse(1500, 500, 2000));

        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willReturn(ResponseEntity.status(200).body(response));

        // when
        Answer answer = client.ask(messages);

        // then
        assertThat(answer.content()).isEqualTo("답변");
    }

    @Test
    void 질문의_토큰이_허용치를_넘어_GPT_API_에서_오류가_반환된다면_이를_처리한다() {
        // given
        List<Message> messages = List.of(question("Q1"), answer("A1"));
        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, OVER_MAX_TOKEN_CODE));

        // when & then
        assertThrows(IllegalArgumentException.class, () -> client.ask(messages));
    }

    @Test
    void GPT_API_에_문제가_있는_경우_예외처리() {
        List<Message> messages = List.of(question("Q1"), answer("A1"));
        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willThrow(new RestClientException("some problem"));

        // when & then
        assertThrows(IllegalStateException.class, () -> client.ask(messages));
    }
}
