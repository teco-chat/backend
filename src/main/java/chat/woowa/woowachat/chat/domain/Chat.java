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
    private Messages messages = new Messages();

    public Chat(final GptModel model,
                final SettingMessage settingMessage,
                final String title,
                final Long memberId,
                final Message message) {
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
        validateTokenSize(message);
        this.messages = new Messages(message);
    }

    protected Chat() {
    }

    private void validateTokenSize(final Message message) {
        if (model.maxTokens() - FREE_TOKEN <= message.token()) {
            throw new TokenSizeBigException();
        }
    }

    public void addMessage(final Message message) {
        validateTokenSize(message);
        this.messages.add(message);
    }

    /**
     * [모델의 최대 토큰 - FREE_TOKEN(2000)] 반환
     */
    public List<Message> messagesWithFreeToken() {
        final List<Message> result = new ArrayList<>();
        result.add(Message.system(settingMessage.message(), 0));
        result.addAll(messages.lessOrEqualThan(model.maxTokens() - FREE_TOKEN));
        return result;
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

    public List<Message> messages() {
        return new ArrayList<>(messages.messages());
    }
}
