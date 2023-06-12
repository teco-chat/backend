package chat.teco.tecochat.chat.domain.chat;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class QuestionAndAnswers {

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    private List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();

    protected QuestionAndAnswers() {
    }

    public QuestionAndAnswers(QuestionAndAnswer... questionAndAnswers) {
        this(Arrays.asList(questionAndAnswers));
    }

    public QuestionAndAnswers(List<QuestionAndAnswer> questionAndAnswers) {
        this.questionAndAnswers.addAll(questionAndAnswers);
    }

    public void add(QuestionAndAnswer questionAndAnswer) {
        questionAndAnswers.add(questionAndAnswer);
    }

    public QuestionAndAnswers last3QuestionAndAnswers() {
        int size = questionAndAnswers.size();
        if (size < 3) {
            return this;
        }
        return new QuestionAndAnswers(questionAndAnswers.subList(size - 3, size));
    }

    public List<Message> messagesWithSettingMessage(SettingMessage settingMessage) {
        List<Message> result = new ArrayList<>();
        result.add(settingMessage);
        for (QuestionAndAnswer qna : questionAndAnswers) {
            result.add(qna.question());
            result.add(qna.answer());
        }
        return result;
    }

    public List<QuestionAndAnswer> questionAndAnswers() {
        return new ArrayList<>(questionAndAnswers);
    }
}
