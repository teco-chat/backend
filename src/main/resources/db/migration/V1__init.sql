CREATE TABLE chat
(
    id              BIGINT AUTO_INCREMENT,
    created_at      TIMESTAMP(6),
    member_id       BIGINT       NOT NULL,
    model           VARCHAR(255) NOT NULL,
    setting_message VARCHAR(255),
    title           VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE member
(
    id         BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6),
    course     VARCHAR(255) NOT NULL,
    name       VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);


CREATE TABLE question_and_answer
(
    id         BIGINT AUTO_INCREMENT,
    created_at TIMESTAMP(6),
    answer     LONGTEXT NOT NULL,
    question   LONGTEXT NOT NULL,
    token      INT      NOT NULL,
    chat_id    BIGINT,
    PRIMARY KEY (id)
);

ALTER TABLE member
    ADD CONSTRAINT unique_member_name UNIQUE (name);

ALTER TABLE question_and_answer
    ADD CONSTRAINT FK_chat_id
        FOREIGN KEY (chat_id)
            REFERENCES chat (id);
