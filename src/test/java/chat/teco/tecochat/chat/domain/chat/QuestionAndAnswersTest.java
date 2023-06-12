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
    void 마지막_3개의_질문과_답변만을_반환한다() {
        // given
        final QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers(
                new QuestionAndAnswer(
                        "질문1",
                        "답변1"
                ),
                new QuestionAndAnswer(
                        "질문2",
                        "답변2"
                ),
                new QuestionAndAnswer(
                        "질문3",
                        "답변3"
                ),
                new QuestionAndAnswer(
                        "질문4",
                        "답변4"
                )
        );

        // when
        QuestionAndAnswers last3 = questionAndAnswers.last3QuestionAndAnswers();

        // then
        assertThat(last3.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("질문2"), question("질문3"), question("질문4"));
    }

    @Test
    void 질문답변이_3개보다_적다면_전부_반환한다() {
        // given
        final QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers(
                new QuestionAndAnswer(
                        "질문1",
                        "답변1"
                ),
                new QuestionAndAnswer(
                        "질문2",
                        "답변2"
                )
                );

        // when
        QuestionAndAnswers last3 = questionAndAnswers.last3QuestionAndAnswers();

        // then
        assertThat(last3.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("질문1"), question("질문2"));
    }
}
