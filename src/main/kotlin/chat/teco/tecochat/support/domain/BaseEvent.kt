package chat.teco.tecochat.support.domain

import java.time.LocalDateTime

abstract class BaseEvent(
    val eventDateTime: LocalDateTime,
) {

    constructor() : this(
        eventDateTime = LocalDateTime.now()
    )

    abstract fun history(): BaseEventHistory
}
