create table chat_like
(
    id         BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6),
    chat_id    BIGINT NOT NULL,
    member_id  BIGINT NOT NULL,
    PRIMARY KEY (id)
);
