package chat.woowa.woowachat.chat.domain;

import static jakarta.persistence.EnumType.STRING;

import chat.woowa.woowachat.chat.exception.TokenSizeBigException;
import chat.woowa.woowachat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat extends BaseEntity {

    public static final int FREE_TOKEN = 2000;

    @Enumerated(STRING)
    @Column(nullable = false)
    private GptModel model;

    @Enumerated(STRING)
    private SettingMessage settingMessage;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long memberId;

    @Embedded
    private QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers();

    public Chat(final GptModel model,
                final SettingMessage settingMessage,
                final String title,
                final Long memberId) {
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
    }

    protected Chat() {
    }

    public void addQuestionAndAnswer(final QuestionAndAnswer questionAndAnswer) {
        validateTokenSize(questionAndAnswer);
        this.questionAndAnswers.add(questionAndAnswer);
    }

    private void validateTokenSize(final QuestionAndAnswer questionAndAnswer) {
        if (model.maxTokens() - FREE_TOKEN < questionAndAnswer.token()) {
            throw new TokenSizeBigException();
        }
    }

    /**
     * [모델의 최대 토큰 - FREE_TOKEN(2000)] 반환
     */
    public List<Message> messagesWithFreeToken() {
        final List<Message> result = new ArrayList<>();
        result.add(settingMessage);
        final List<QuestionAndAnswer> lessOrEqualThan =
                questionAndAnswers.lessOrEqualThan(model.maxTokens() - FREE_TOKEN);
        for (final QuestionAndAnswer qna : lessOrEqualThan) {
            result.add(qna.question());
            result.add(qna.answer());
        }
        return result;
    }

    public int totalToken() {
        return questionAndAnswers.calculateTokenSum();
    }

    public String modelName() {
        return model.modelName();
    }

    public String title() {
        return title;
    }

    public SettingMessage settingMessage() {
        return settingMessage;
    }

    public Long memberId() {
        return memberId;
    }

    public List<QuestionAndAnswer> questionAndAnswers() {
        return new ArrayList<>(questionAndAnswers.questionAndAnswers());
    }
}
