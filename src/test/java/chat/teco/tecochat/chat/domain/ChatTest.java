package chat.teco.tecochat.chat.domain;

import static chat.teco.tecochat.chat.domain.Answer.answer;
import static chat.teco.tecochat.chat.domain.Chat.FREE_TOKEN;
import static chat.teco.tecochat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.domain.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.fixture.ChatFixture;
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
        final Chat chat = ChatFixture.defaultChat();

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
        final Chat chat = ChatFixture.chat(messages);

        // when
        final List<Message> messageInterfaces = chat.qnaWithFreeToken()
                .messagesWithSettingMessage(chat.settingMessage());

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
        final Chat chat = ChatFixture.chat(messages);

        // when
        final List<Message> result = chat.qnaWithFreeToken()
                .messagesWithSettingMessage(chat.settingMessage());

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly(chat.settingMessage().message(),
                        "Q3",
                        "A3"
                );
    }
}
