package chat.teco.tecochat.comment.domain;


import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_DELETE_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_UPDATE_COMMENT;

import chat.teco.tecochat.comment.execption.CommentException;
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

    public Comment(final Long chatId, final Long memberId, final String content) {
        this.chatId = chatId;
        this.memberId = memberId;
        this.content = content;
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

    public void update(final Long memberId, final String content) {
        if (!this.memberId.equals(memberId)) {
            throw new CommentException(NO_AUTHORITY_UPDATE_COMMENT);
        }

        this.content = content;
    }

    public void validateDelete(final Long memberId) {
        if (!this.memberId.equals(memberId)) {
            throw new CommentException(NO_AUTHORITY_DELETE_COMMENT);
        }
    }
}
