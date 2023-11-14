package chat.teco.tecochat.application

import chat.teco.tecochat.member.domain.Course
import chat.teco.tecochat.member.domain.Member

data class MemberData(
    val name: String,
    val course: Course
) {

    fun toMember(): Member {
        return Member(name, course)
    }
}
