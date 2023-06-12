package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Answer.answer;
import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.GPT_API_ERROR;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.QUESTION_SIZE_TOO_BIG;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.domain.chat.GptClient.ChatCompletionRequest;
import chat.teco.tecochat.chat.domain.chat.GptClient.ChatCompletionRequest.MessageRequest;
import chat.teco.tecochat.chat.domain.chat.GptClient.ChatCompletionResponse;
import chat.teco.tecochat.chat.domain.chat.GptClient.ChatCompletionResponse.ChoiceResponse;
import chat.teco.tecochat.chat.domain.chat.GptClient.ChatCompletionResponse.MessageResponse;
import chat.teco.tecochat.chat.domain.chat.GptClient.ChatCompletionResponse.UsageResponse;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.exception.BaseExceptionType;
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
    void 받아온_응답을_바탕으로_QnA의_토큰_수를_계산하여_반환한다() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(
                        question("Q1"),
                        answer("A1"),
                        1000
                )
        );
        final Chat chat = ChatFixture.chat(messages);
        final ChatCompletionResponse response = new ChatCompletionResponse("", ",", "",
                List.of(new ChoiceResponse(1L,
                        new MessageResponse("assistant", "답변"),
                        "stop")),
                new UsageResponse(1500, 500, 2000));

        given(restTemplate.postForEntity(any(String.class), any(), any())).willReturn(
                ResponseEntity.status(200).body(response));

        // when
        final QuestionAndAnswer qna = client.ask(chat, question("질문"));

        // then
        // 기존 채팅 [1000], API 결과 전체 토큰 [2000] -> [2000] - [1000] = 1000
        assertThat(qna.token()).isEqualTo(1000);
    }

    @Test
    void 질문의_토큰이_허용치를_넘어_GPT_API_에서_오류가_반환된다면_이를_처리한다() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(
                        question("Q1"),
                        answer("A1"),
                        1000
                )
        );
        final Chat chat = ChatFixture.chat(messages);
        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, OVER_MAX_TOKEN_CODE));

        // when & then
        final BaseExceptionType exceptionType = assertThrows(ChatException.class,
                () -> client.ask(chat, question("질문"))
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(QUESTION_SIZE_TOO_BIG);
    }

    @Test
    void GPT_API_에_문제가_있는_경우_예외처리() {
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(  // 제외
                        question("Q1"),
                        answer("A1"),
                        100
                )
        );
        final Chat chat = ChatFixture.chat(messages);
        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willThrow(new RestClientException("some problem"));

        // when & then
        final BaseExceptionType exceptionType = assertThrows(ChatException.class,
                () -> client.ask(chat, question("질문"))
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(GPT_API_ERROR);
    }

    @Test
    void 최근_3개의_질문답변만을_함께_전송한다() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer("Q1", "A1"),
                new QuestionAndAnswer("Q2", "A2"),
                new QuestionAndAnswer("Q3", "A3"),
                new QuestionAndAnswer("Q4", "A4"),
                new QuestionAndAnswer("Q5", "A5")
        );
        final Chat chat = ChatFixture.chatWithModel(GPT_3_5_TURBO, messages);

        // when
        final ChatCompletionRequest from = ChatCompletionRequest.of(chat, question("질문"));

        // then
        assertThat(from.messages()).extracting(MessageRequest::content)
                .containsExactly(
                        SettingMessage.BACK_END_SETTING.message(),
                        "Q3", "A3",
                        "Q4", "A4",
                        "Q5", "A5",
                        "질문");
    }
}
