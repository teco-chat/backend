package chat.teco.tecochat.chat.domain.keyword;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Keyword extends BaseEntity {

    private String keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    protected Keyword() {
    }

    public Keyword(final String keyword, final Chat chat) {
        this.keyword = keyword;
        this.chat = chat;
    }

    public String keyword() {
        return keyword;
    }

    public Chat chat() {
        return chat;
    }

    @Override
    public String toString() {
        return "Keyword{" +
                "keyword='" + keyword + '\'' +
                ", chat=" + chat +
                '}';
    }
}
