package chat.teco.tecochat.like.chatlike.domain;

import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class ChatLike extends BaseEntity {

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long chatId;

    protected ChatLike() {
    }

    public ChatLike(final Long memberId, final Long chatId) {
        this.memberId = memberId;
        this.chatId = chatId;
    }

    public Long memberId() {
        return memberId;
    }

    public Long chatId() {
        return chatId;
    }
}
