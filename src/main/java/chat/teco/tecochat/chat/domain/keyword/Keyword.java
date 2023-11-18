package chat.teco.tecochat.chat.domain.keyword;

import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.support.domain.BaseEntity;
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

    public Keyword(String keyword, Chat chat) {
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
        return "Keyword=[" + keyword + "]";
    }

    public Keyword copy(Chat copiedChat) {
        return new Keyword(keyword, copiedChat);
    }
}
