package chat.teco.tecochat.chat.domain;

import static jakarta.persistence.EnumType.STRING;

import chat.teco.tecochat.chat.exception.TokenSizeBigException;
import chat.teco.tecochat.common.entity.BaseEntity;
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

    protected Chat() {
    }

    public Chat(final GptModel model,
                final SettingMessage settingMessage,
                final String title,
                final Long memberId) {
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
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
    public QuestionAndAnswers qnaWithFreeToken() {
        return questionAndAnswers.lessOrEqualThan(model.maxTokens() - FREE_TOKEN);
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
