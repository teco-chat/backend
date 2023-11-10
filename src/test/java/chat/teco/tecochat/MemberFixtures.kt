package chat.teco.tecochat

import chat.teco.tecochat.member.domain.Course
import chat.teco.tecochat.member.domain.Member

const val NAME = "mallang"
val COURSE = Course.BACKEND

fun createMember(
    course: Course = COURSE,
    name: String = NAME,
    id: Long = 0L
): Member {
    return Member(id, name, course)
}
