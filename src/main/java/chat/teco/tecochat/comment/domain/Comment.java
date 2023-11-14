package chat.teco.tecochat.comment.domain;

import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Comment extends BaseEntity {

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    protected Comment() {
    }

    public Comment(Long id, Long chatId, Long memberId, String content) {
        super(id);
        this.chatId = chatId;
        this.memberId = memberId;
        this.content = content;
    }

    public Comment(Long chatId, Long memberId, String content) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.content = content;
    }

    public void update(Long memberId, String content) {
        if (!this.memberId.equals(memberId)) {
            throw new IllegalStateException("댓글을 수정할 수 없습니다.");
        }
        this.content = content;
    }

    public void validateDelete(Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new IllegalStateException("댓글을 삭제할 수 없습니다.");
        }
    }

    public Long chatId() {
        return chatId;
    }

    public Long memberId() {
        return memberId;
    }

    public String content() {
        return content;
    }
}
