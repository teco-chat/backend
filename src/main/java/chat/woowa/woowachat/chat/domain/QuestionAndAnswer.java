package chat.woowa.woowachat.chat.domain;

import static java.util.Objects.requireNonNull;

import chat.woowa.woowachat.common.entity.BaseEntity;
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

    public QuestionAndAnswer(final Question question, final Answer answer, final int token) {
        this.question = requireNonNull(question);
        this.answer = requireNonNull(answer);
        this.token = token;
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
