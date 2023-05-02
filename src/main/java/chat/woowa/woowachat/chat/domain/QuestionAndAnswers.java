package chat.woowa.woowachat.chat.domain;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

@Embeddable
public class QuestionAndAnswers {

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    private List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();

    protected QuestionAndAnswers() {
    }

    public QuestionAndAnswers(final QuestionAndAnswer... questionAndAnswers) {
        this(Arrays.asList(questionAndAnswers));
    }

    public QuestionAndAnswers(final List<QuestionAndAnswer> questionAndAnswers) {
        this.questionAndAnswers.addAll(questionAndAnswers);
    }

    public void add(final QuestionAndAnswer questionAndAnswer) {
        questionAndAnswers.add(questionAndAnswer);
    }

    public QuestionAndAnswers lessOrEqualThan(final int token) {
        final Deque<QuestionAndAnswer> result = new ArrayDeque<>(this.questionAndAnswers);
        int tokenSum = calculateTokenSum();

        while (tokenSum > token) {
            final QuestionAndAnswer message = result.removeFirst();
            tokenSum -= message.token();
        }

        return new QuestionAndAnswers(new ArrayList<>(result));
    }

    public int calculateTokenSum() {
        return questionAndAnswers.stream()
                .mapToInt(QuestionAndAnswer::token)
                .sum();
    }

    public List<QuestionAndAnswer> questionAndAnswers() {
        return new ArrayList<>(questionAndAnswers);
    }
}
