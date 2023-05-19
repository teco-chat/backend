package chat.teco.tecochat.common.event;

import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_history")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", length = 60)
public abstract class BaseEventHistory extends BaseEntity {

    protected LocalDateTime eventDateTime;  // 이벤트 발행 시간

    protected boolean processed;  // 처리 상태

    protected BaseEventHistory() {
    }

    public BaseEventHistory(final LocalDateTime eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public LocalDateTime eventDateTime() {
        return eventDateTime;
    }

    public boolean processed() {
        return processed;
    }

    public void process() {
        this.processed = true;
    }
}
