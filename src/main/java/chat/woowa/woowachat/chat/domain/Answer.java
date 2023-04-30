package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.Role.ASSISTANT;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import java.util.Objects;

@Embeddable
public class Answer implements Message {

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answer;

    private Answer(final String content) {
        this.answer = content;
    }

    protected Answer() {
    }

    public static Answer answer(final String content) {
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Answer answer)) {
            return false;
        }
        return this.answer.equals(answer.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(answer);
    }
}
