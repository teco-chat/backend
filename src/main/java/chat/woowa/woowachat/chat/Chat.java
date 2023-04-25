package chat.woowa.woowachat.chat;

import static jakarta.persistence.EnumType.STRING;

import chat.woowa.woowachat.common.entity.BaseEntity;
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
        this.messages = new Messages(message);
    }

    protected Chat() {
    }

    public void addMessage(final Message message) {
        this.messages.add(message);
    }

    public List<Message> lessThan(final int token) {
        final List<Message> result = new ArrayList<>();
        result.add(Message.system(settingMessage.message(), 0));
        result.addAll(messages.lessThan(token));
        return result;
    }

    public String title() {
        return title;
    }

    public GptModel model() {
        return model;
    }

    public SettingMessage settingMessage() {
        return settingMessage;
    }

    public Long memberId() {
        return memberId;
    }

    public List<Message> messages() {
        return messages.messages();
    }
}
