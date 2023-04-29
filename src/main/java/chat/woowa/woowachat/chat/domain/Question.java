package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.Role.USER;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import java.util.Objects;

@Embeddable
public class Question implements Message {

    @Lob
    @Column(nullable = false)
    private String question;

    private Question(final String content) {
        this.question = content;
    }

    protected Question() {
    }

    public static Question question(final String content) {
        return new Question(content);
    }

    @Override
    public String roleName() {
        return USER.roleName();
    }

    @Override
    public String content() {
        return question;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof final Question question)) {
            return false;
        }
        return this.question.equals(question.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question);
    }
}
