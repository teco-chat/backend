package chat.woowa.woowachat.member.service;

import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import chat.woowa.woowachat.member.dto.SignUpDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void signUp(final SignUpDto signUpDto) {
        final Member member = signUpDto.toDomain();
        if (!memberRepository.existsByName(member.name())) {
            memberRepository.save(member);
        }
    }
}
