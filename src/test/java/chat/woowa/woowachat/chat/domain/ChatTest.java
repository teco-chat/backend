package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.Answer.answer;
import static chat.woowa.woowachat.chat.domain.Chat.FREE_TOKEN;
import static chat.woowa.woowachat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.woowa.woowachat.chat.domain.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.woowa.woowachat.chat.exception.TokenSizeBigException;
import chat.woowa.woowachat.chat.fixture.Chat2Fixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Chat 은")
class ChatTest {

    @Test
    void QnA를_추가할_수_있다() {
        // given
        final Chat chat = Chat2Fixture.defaultChat();

        // when
        chat.addQuestionAndAnswer(new QuestionAndAnswer(
                question("안녕"),
                answer("응 안녕"),
                1
        ));

        // then
        final QuestionAndAnswer qna = chat.questionAndAnswers().get(0);
        assertAll(
                () -> assertThat(qna.answer())
                        .isEqualTo(answer("응 안녕")),
                () -> assertThat(qna.question())
                        .isEqualTo(question("안녕"))
        );
    }

    @Test
    void 메세지를_추가_시_최대토큰에서_가용토큰을_뺀_길이만큼의_메세지가_들어오면_예외() {
        // given
        final Chat chat = Chat2Fixture.chatWithModel(GPT_3_5_TURBO,
                new QuestionAndAnswer(
                        question("안녕"),
                        answer("응 안녕"),
                        0
                ));

        // when & then
        assertThatThrownBy(() ->
                chat.addQuestionAndAnswer(new QuestionAndAnswer(
                        question("안녕"),
                        answer("응안녕"),
                        GPT_3_5_TURBO.maxTokens() - FREE_TOKEN + 1
                ))
        ).isInstanceOf(TokenSizeBigException.class);
    }

    @Test
    void 주어진_가용_토큰만큼의_공간_이상을_확보하도록_오래된_순으로_메시지를_제외한_후_세팅_메세지를_포함하여_반환한다() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(
                        question("Q1"),
                        answer("A1"),
                        1500
                ),
                new QuestionAndAnswer(
                        question("Q2"),
                        answer("A2"),
                        200
                ),
                new QuestionAndAnswer(
                        question("Q3"),
                        answer("A3"),
                        1500
                ));
        final Chat chat = Chat2Fixture.chat(messages);

        // when
        final List<Message> messageInterfaces = chat.messagesWithFreeToken();

        // then
        assertThat(messageInterfaces).extracting(Message::content)
                .containsExactly(
                        chat.settingMessage().message(),
                        "Q2",
                        "A2",
                        "Q3",
                        "A3");
    }

    @Test
    void 주어진_가용_토큰만큼의_공간_이상을_확보하도록_오래된_순으로_메시지를_제외한_후_세팅_메세지를_포함하여_반환한다_엣지_케이스() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(
                        question("Q1"),
                        answer("A1"),
                        1500
                ),
                new QuestionAndAnswer(
                        question("Q2"),
                        answer("A2"),
                        200
                ),
                new QuestionAndAnswer(
                        question("Q3"),
                        answer("A3"),
                        GPT_3_5_TURBO.maxTokens() - FREE_TOKEN
                ));
        final Chat chat = Chat2Fixture.chat(messages);

        // when
        final List<Message> result = chat.messagesWithFreeToken();

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly(chat.settingMessage().message(),
                        "Q3",
                        "A3"
                );
    }
}
