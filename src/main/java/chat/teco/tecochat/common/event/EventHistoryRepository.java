package chat.teco.tecochat.common.event;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EventHistoryRepository extends JpaRepository<BaseEventHistory, Long> {
}
