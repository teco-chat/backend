package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;
import static chat.teco.tecochat.chat.domain.Answer.answer;
import static chat.teco.tecochat.chat.domain.Question.question;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import chat.teco.tecochat.acceptance.AcceptanceTestSteps;
import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.GptClient;
import chat.teco.tecochat.chat.domain.Question;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.chat.dto.AskRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("NonAsciiCharacters")
public class ChatBuilder {

    private final GptClient gptClient;
    private String 크루명;
    private String 질문내용;
    private String 답변내용;
    private String[] 키워드들;

    public ChatBuilder(final GptClient gptClient) {
        this.gptClient = gptClient;
    }

    public static ChatBuilder chatBuilder(final GptClient gptClient) {
        return new ChatBuilder(gptClient);
    }

    public GptAnswerBuilder 첫_질문_요청(final String 크루명, final String 질문내용) {
        this.크루명 = 크루명;
        this.질문내용 = 질문내용;
        return new GptAnswerBuilder();
    }

    public class GptAnswerBuilder {

        public KeywordBuilder GPT_의_답변_세팅(final String 답변) {
            답변내용 = 답변;
            return new KeywordBuilder();
        }
    }

    public class KeywordBuilder {

        public Finish 키워드_세팅(final String... 키워드) {
            키워드들 = 키워드;
            return new Finish();
        }
    }

    public class Finish {

        public ExtractableResponse<Response> 요청을_보낸다() {
            GPT_의_응답을_지정한다();
            return AcceptanceTestSteps.given(크루명)
                    .body(toJson(new AskRequest(질문내용)))
                    .when()
                    .post("/chats")
                    .then()
                    .log().all()
                    .extract();
        }

        private void GPT_의_응답을_지정한다() {
            final String 응답 = Arrays.stream(키워드들)
                    .collect(Collectors.joining("||"));
            given(gptClient.ask(any(Chat.class), any(Question.class)))
                    .willReturn(
                            new QuestionAndAnswer(question(질문내용), answer(답변내용), 10),
                            new QuestionAndAnswer(question("키워드 추출"), answer(응답), 10));
        }
    }
}
