package chat.teco.tecochat.common.event;

public abstract class EventHandler<EVENT extends BaseEvent> {

    protected final EventHistoryRepository eventHistoryRepository;

    protected EventHandler(final EventHistoryRepository eventHistoryRepository) {
        this.eventHistoryRepository = eventHistoryRepository;
    }

    public abstract void handle(final EVENT event);
}
