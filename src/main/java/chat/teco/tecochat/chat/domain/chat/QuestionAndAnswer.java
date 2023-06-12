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

    protected QuestionAndAnswer() {
    }

    public QuestionAndAnswer(String question, String answer) {
        this(Question.question(question), Answer.answer(answer));
    }

    public QuestionAndAnswer(Question question, Answer answer) {
        this.question = requireNonNull(question);
        this.answer = requireNonNull(answer);
    }

    public QuestionAndAnswer copy() {
        return new QuestionAndAnswer(question, answer);
    }

    public Question question() {
        return question;
    }

    public Answer answer() {
        return answer;
    }
}
