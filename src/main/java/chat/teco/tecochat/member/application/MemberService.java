package chat.teco.tecochat.member.application;

import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.dto.SignUpDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void signUp(final SignUpDto signUpDto) {
        final Member member = signUpDto.toDomain();
        memberRepository.findByName(member.name())
                .ifPresentOrElse(
                        saved -> saved.changeCourse(member.course()),
                        () -> memberRepository.save(member)
                );
    }
}
