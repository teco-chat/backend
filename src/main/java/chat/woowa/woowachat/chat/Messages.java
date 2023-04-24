package chat.woowa.woowachat.chat;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

@Embeddable
public class Messages {

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id")  // 연관관계의 주인을 chat 으로 지정
    private List<Message> messages = new ArrayList<>();

    public Messages(final List<Message> messages) {
        this.messages.addAll(messages);
    }

    public Messages(final Message... messages) {
        this(Arrays.asList(messages));
    }

    protected Messages() {
    }

    public List<Message> messages() {
        return messages;
    }

    public void add(final Message message) {
        messages.add(message);
    }
}
