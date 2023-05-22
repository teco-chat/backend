package chat.teco.tecochat.chat.domain.chat;

import static java.util.Objects.requireNonNull;

import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;

@Entity
public class QuestionAndAnswer extends BaseEntity {

    @Embedded
    private Question question;

    @Embedded
    private Answer answer;

    private int token;

    protected QuestionAndAnswer() {
    }

    public QuestionAndAnswer(final String question, final String answer, final int token) {
        this(Question.question(question), Answer.answer(answer), token);
    }

    public QuestionAndAnswer(final Question question, final Answer answer, final int token) {
        this.question = requireNonNull(question);
        this.answer = requireNonNull(answer);
        this.token = token;
    }

    public QuestionAndAnswer copy() {
        return new QuestionAndAnswer(question, answer, token);
    }

    public Question question() {
        return question;
    }

    public Answer answer() {
        return answer;
    }

    public int token() {
        return token;
    }
}
