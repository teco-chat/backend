package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Answer.answer;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("QuestionAndAnswers(질문 답변들) 은(는)")
class QuestionAndAnswersTest {

    @Test
    void QnA_를_저장할_수_있다() {
        // given
        QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers();

        // when
        questionAndAnswers.add(new QuestionAndAnswer(
                question("질문"),
                answer("답변"),
                1
        ));

        // then
        assertThat(questionAndAnswers.questionAndAnswers())
                .hasSize(1);
    }

    @Test
    void 입력받은_토큰보다_메시지의_토큰_총합이_같거나_낮도록_오래된_순으로_메시지를_제외한_후_반환한다() {
        // given
        final int token = 2000;
        final QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers(
                new QuestionAndAnswer(
                        question("질문1"),
                        answer("답변1"),
                        1500
                ),
                new QuestionAndAnswer(
                        question("질문2"),
                        answer("답변2"),
                        200
                ),
                new QuestionAndAnswer(
                        question("질문3"),
                        answer("답변3"),
                        1500
                )
        );

        // when
        final QuestionAndAnswers result = questionAndAnswers.lessOrEqualThan(token);

        // then
        assertThat(result.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("질문2"), question("질문3"));
    }

    @Test
    void 입력받은_토큰보다_메시지의_토큰_총합이_낮도록_오래된_순으로_메시지를_제외한_후_반환한다_엣지_케이스() {
        // given
        final int token = 1000;
        final QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers(
                new QuestionAndAnswer(
                        question("질문1"),
                        answer("답변1"),
                        token
                )
        );

        // when
        final QuestionAndAnswers result = questionAndAnswers.lessOrEqualThan(token);

        // then
        assertThat(result.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("질문1"));
    }
}
