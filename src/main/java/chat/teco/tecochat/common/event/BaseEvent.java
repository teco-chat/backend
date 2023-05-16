package chat.teco.tecochat.common.event;

import java.time.LocalDateTime;

public abstract class BaseEvent {

    protected final LocalDateTime eventDateTime;

    public BaseEvent() {
        this.eventDateTime = LocalDateTime.now();
    }

    public abstract BaseEventHistory history();

    public LocalDateTime eventDateTime() {
        return eventDateTime;
    }
}
