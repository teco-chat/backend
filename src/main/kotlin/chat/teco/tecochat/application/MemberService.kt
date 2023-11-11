package chat.teco.tecochat.application

import chat.teco.tecochat.domain.member.MemberRepository
import chat.teco.tecochat.member.application.dto.SignUpCommand
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class MemberService(
    private val memberRepository: MemberRepository
) {

    fun signUp(signUpCommand: SignUpCommand) {
        val member = signUpCommand.toMember()

        memberRepository.findByName(member.name())?.apply {
            changeCourse(member.course())
        } ?: run {
            memberRepository.save(member)
        }
    }
}
