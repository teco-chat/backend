package chat.teco.tecochat.domain.member

import chat.teco.tecochat.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Entity
class Member(
    @Column(nullable = false, unique = true)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var course: Course,

    id: Long = 0L,
) : BaseEntity(id) {

    fun changeCourse(course: Course) {
        this.course = course
    }
}
