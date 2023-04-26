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
public class Messages {

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    private List<Message> messages = new ArrayList<>();

    public Messages(final Message... messages) {
        this(Arrays.asList(messages));
    }

    public Messages(final List<Message> messages) {
        this.messages.addAll(messages);
    }

    protected Messages() {
    }

    public void add(final Message message) {
        messages.add(message);
    }

    public List<Message> lessOrEqualThan(final int token) {
        final Deque<Message> result = new ArrayDeque<>(this.messages);
        int tokenSum = calculateTokenSum();

        while (tokenSum > token) {
            final Message message = result.removeFirst();
            tokenSum -= message.token();
        }

        return new ArrayList<>(result);
    }

    private int calculateTokenSum() {
        return messages.stream()
                .mapToInt(Message::token)
                .sum();
    }

    public List<Message> messages() {
        return messages;
    }
}
