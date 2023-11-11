package chat.teco.tecochat.application

import chat.teco.tecochat.domain.member.MemberRepository
import chat.teco.tecochat.member.domain.Course
import createMember
import createMemberData
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class MemberServiceTest : BehaviorSpec({

    val memberRepository = mockk<MemberRepository>()

    val memberService = MemberService(memberRepository)

    Given("이미 닉네임에 해당하는 회원이 존재하는 경우") {
        val signUpCommand = createMemberData(Course.ANDROID, "mallang")
        val member = createMember()
        every { memberRepository.findByName(any()) } returns member

        When("회원가입을 진행하면") {
            memberService.signUp(signUpCommand)

            Then("입력받은 코스로 변경된다") {
                member.course() shouldBe signUpCommand.course
            }
        }
    }

    Given("닉네임에 해당하는 회원이 존재하지 않는 경우") {
        val signUpCommand = createMemberData(Course.ANDROID, "mallang")
        every { memberRepository.findByName(any()) } returns null
        every { memberRepository.save(any()) } returns createMember()

        When("회원가입을 진행하면") {
            memberService.signUp(signUpCommand)

            Then("회원을 저장한다") {
                verify(exactly = 1) {
                    memberRepository.save(any())
                }
            }
        }
    }
})
