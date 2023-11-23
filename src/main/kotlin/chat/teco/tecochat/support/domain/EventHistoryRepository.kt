package chat.teco.tecochat.support.domain

import org.springframework.data.jpa.repository.JpaRepository

interface EventHistoryRepository : JpaRepository<BaseEventHistory, Long>
