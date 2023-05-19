package chat.teco.tecochat.member.application.service;

import chat.teco.tecochat.member.application.usecase.SignUpUseCase;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class MemberService implements SignUpUseCase {

    private final MemberRepository memberRepository;

    @Override
    public void signUp(SignUpCommand signUpCommand) {
        Member member = signUpCommand.toDomain();
        memberRepository.findByName(member.name())
                .ifPresentOrElse(
                        saved -> saved.changeCourse(member.course()),
                        () -> memberRepository.save(member)
                );
    }
}
