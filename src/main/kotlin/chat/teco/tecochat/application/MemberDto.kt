package chat.teco.tecochat.application

import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.Member

data class MemberData(
    val name: String,
    val course: Course,
) {

    fun toMember(): Member {
        return Member(name, course)
    }
}
