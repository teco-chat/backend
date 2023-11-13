package chat.teco.tecochat

import chat.teco.tecochat.application.SignUpRequest
import chat.teco.tecochat.member.domain.Course
import chat.teco.tecochat.member.domain.Member

const val MEMBER_ID = 1L
const val NAME = "mallang"
val COURSE = Course.BACKEND

fun createMember(
    name: String = NAME,
    course: Course = COURSE,
    id: Long = 0L
): Member {
    return Member(id, name, course)
}

fun createSignUpRequest(
    course: Course = COURSE,
    name: String = NAME,
): SignUpRequest {
    return SignUpRequest(name, course)
}
