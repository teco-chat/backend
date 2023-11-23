package chat.teco.tecochat.support.domain

import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.Table
import java.time.LocalDateTime


@Entity
@Table(name = "event_history")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", length = 60)
abstract class BaseEventHistory(
    var eventDateTime: LocalDateTime,
    var processed: Boolean = false,
) : BaseEntity(0L) {

    constructor() : this(
        eventDateTime = LocalDateTime.now(),
        processed = false
    )

    constructor(eventDateTime: LocalDateTime) : this(
        eventDateTime = eventDateTime,
        processed = false
    )

    fun process() {
        processed = true
    }
}
