package chat.teco.tecochat

import chat.teco.tecochat.application.MemberData
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.Member

const val MEMBER_ID = 1L
const val NAME = "mallang"
val COURSE = Course.BACKEND

fun createMember(
    name: String = NAME,
    course: Course = COURSE,
    id: Long = 0L,
): Member {
    return Member(name, course, id)
}

fun createSignUpRequest(
    course: Course = COURSE,
    name: String = NAME,
): MemberData {
    return MemberData(name, course)
}
