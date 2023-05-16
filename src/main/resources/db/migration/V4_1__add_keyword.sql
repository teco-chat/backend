create table keyword
(
    id         BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6),
    keyword    VARCHAR(255) NOT NULL,
    chat_id    BIGINT       NOT NULL,
    PRIMARY KEY (id)
);
