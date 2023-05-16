create table event_history
(
    event_type      VARCHAR(60) NOT NULL,
    id              BIGINT AUTO_INCREMENT,
    created_at      TIMESTAMP(6),
    event_date_time TIMESTAMP(6),
    processed       BOOLEAN     NOT NULL,
    chat_id         BIGINT,
    primary key (id)
)
