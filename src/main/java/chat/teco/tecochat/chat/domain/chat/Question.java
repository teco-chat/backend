package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Role.USER;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Question implements Message {

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String question;

    // TODO 빈값 검증
    private Question(String content) {
        this.question = content;
    }

    protected Question() {
    }

    public static Question question(String content) {
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question question)) {
            return false;
        }
        return this.question.equals(question.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question);
    }
}
