create table comment
(
    id         BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6),
    chat_id    BIGINT   NOT NULL,
    content    LONGTEXT NOT NULL,
    member_id  BIGINT   NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE comment
    ADD CONSTRAINT FK_comment_chat_id
        FOREIGN KEY (chat_id)
            REFERENCES chat (id);
