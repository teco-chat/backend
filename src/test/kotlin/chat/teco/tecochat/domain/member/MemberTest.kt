package chat.teco.tecochat.domain.member

import chat.teco.tecochat.createMember
import chat.teco.tecochat.domain.member.Course.BACKEND
import chat.teco.tecochat.domain.member.Course.FRONTEND
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MemberTest : StringSpec({

    "사용자의 코스를 변경한다" {
        val member = createMember(course = BACKEND)

        member.changeCourse(FRONTEND)

        member.course shouldBe FRONTEND
    }
})
