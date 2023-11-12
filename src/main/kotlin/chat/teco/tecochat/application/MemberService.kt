package chat.teco.tecochat.application

import chat.teco.tecochat.domain.member.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository
) {

    fun signUp(signUpRequest: SignUpRequest) {
        val member = signUpRequest.toMember()

        memberRepository.findByName(member.name())?.apply {
            changeCourse(member.course())
        } ?: run {
            memberRepository.save(member)
        }
    }
}
