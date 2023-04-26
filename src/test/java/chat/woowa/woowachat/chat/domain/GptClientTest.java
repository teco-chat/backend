package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.GptModel.GPT_3_5_TURBO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionRequest;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionRequest.MessageRequest;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse.ChoiceResponse;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse.MessageResponse;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse.UsageResponse;
import chat.woowa.woowachat.chat.fixture.ChatFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("GptClient 은(는)")
class GptClientTest {

    private final HttpHeaders apiKeySettingHeader = new HttpHeaders();
    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final GptClient client = new GptClient(restTemplate, apiKeySettingHeader);


    @Test
    void Chat_의_messagesWithFreeToken_을_호출해야_한다() {
        // given
        final ChatCompletionResponse response = new ChatCompletionResponse("", ",", "",
                List.of(new ChoiceResponse(1L,
                        new MessageResponse("assistant", "답변"),
                        "stop")),
                new UsageResponse(1, 2, 3));

        given(restTemplate.postForEntity(any(String.class), any(), any())).willReturn(
                ResponseEntity.status(200).body(response));
        final Chat chat = mock(Chat.class);

        // when
        client.ask(chat);

        // then
        verify(chat, times(1))
                .messagesWithFreeToken();
    }

    @Test
    void 최대_토큰에서_채팅의_총합_토큰을_뺐을_때_2000이상_남지_않는다면_이에_맞추어_메세지를_제외한다() {
        // given
        final Chat chat = ChatFixture.chatWithModelAndMessages(GPT_3_5_TURBO,
                List.of(
                        Message.user("안녕 1", 1000), // 제외
                        Message.user("안녕2", 1000), // 제외
                        Message.user("안녕3", 1000), // 제외
                        Message.user("안녕4", 1000),
                        Message.user("안녕5", 1000)));

        // when
        final ChatCompletionRequest from = ChatCompletionRequest.from(chat);

        // then
        assertThat(from.messages()).extracting(MessageRequest::content)
                .containsExactly(SettingMessage.BACK_END_SETTING.message(), "안녕4", "안녕5");
    }

}
