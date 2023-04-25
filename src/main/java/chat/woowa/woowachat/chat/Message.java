package chat.woowa.woowachat.chat;

import static chat.woowa.woowachat.chat.Role.ASSISTANT;
import static chat.woowa.woowachat.chat.Role.SYSTEM;
import static chat.woowa.woowachat.chat.Role.USER;
import static jakarta.persistence.EnumType.STRING;

import chat.woowa.woowachat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;

@Entity
public class Message extends BaseEntity {

    @Enumerated(STRING)
    @Column(nullable = false)
    private Role role;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int token;

    private Message(final Role role, final String content, final int token) {
        this.role = role;
        this.content = content;
        this.token = token;
    }

    protected Message() {
    }

    public static Message system(final String content, final int token) {
        return new Message(SYSTEM, content, token);
    }

    public static Message user(final String content, final int token) {
        return new Message(USER, content, token);
    }

    public static Message assistant(final String content, final int token) {
        return new Message(ASSISTANT, content, token);
    }

    public String content() {
        return content;
    }

    public int token() {
        return token;
    }

    public Role role() {
        return role;
    }
}
