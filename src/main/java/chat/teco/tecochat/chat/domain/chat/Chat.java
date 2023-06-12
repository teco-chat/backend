package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NO_AUTHORITY_CHANGE_TITLE;
import static jakarta.persistence.EnumType.STRING;

import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat extends BaseEntity {

    @Enumerated(STRING)
    @Column(nullable = false)
    private GptModel model;

    @Enumerated(STRING)
    private SettingMessage settingMessage;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long memberId;

    private int likeCount;

    private int commentCount;

    @Embedded
    private QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers();

    protected Chat() {
    }

    public Chat(
            Long id,
            GptModel model,
            SettingMessage settingMessage,
            String title,
            Long memberId
    ) {
        super(id);
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
    }

    public Chat(
            GptModel model,
            SettingMessage settingMessage,
            String title,
            Long memberId
    ) {
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
    }

    public void addQuestionAndAnswer(QuestionAndAnswer questionAndAnswer) {
        this.questionAndAnswers.add(questionAndAnswer);
    }

    public QuestionAndAnswers last3QuestionAndAnswers() {
        return questionAndAnswers.last3QuestionAndAnswers();
    }

    public void decreaseLike() {
        likeCount--;
    }

    public void increaseLike() {
        likeCount++;
    }

    public void decreaseComment() {
        commentCount--;
    }

    public void increaseComment() {
        commentCount++;
    }

    public void updateTitle(Long memberId, String title) {
        if (!this.memberId().equals(memberId)) {
            throw new ChatException(NO_AUTHORITY_CHANGE_TITLE);
        }
        this.title = title;
    }

    public Chat copy(Long memberId) {
        Chat copied = new Chat(this.model, this.settingMessage, this.title, memberId);
        for (QuestionAndAnswer questionAndAnswer : questionAndAnswers()) {
            copied.addQuestionAndAnswer(questionAndAnswer.copy());
        }
        return copied;
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

    public int likeCount() {
        return likeCount;
    }

    public int commentCount() {
        return commentCount;
    }
}
