package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Role.ASSISTANT;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Answer implements Message {

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answer;

    private Answer(String content) {
        this.answer = content;
    }

    protected Answer() {
    }

    public static Answer answer(String content) {
        return new Answer(content);
    }

    @Override
    public String roleName() {
        return ASSISTANT.roleName();
    }

    @Override
    public String content() {
        return answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Answer answer)) {
            return false;
        }
        return this.answer.equals(answer.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }
}
