package chat.teco.tecochat.auth.domain;

import static chat.teco.tecochat.auth.exception.AuthenticationExceptionType.NO_REGISTERED_MEMBER;
import static chat.teco.tecochat.common.util.Base64Util.decodeBase64;

import chat.teco.tecochat.auth.exception.AuthenticationException;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Authenticator {

    private final MemberRepository memberRepository;

    public Member authenticateWithBase64(String encodedName) {
        return memberRepository.findByName(decodeBase64(encodedName))
                .orElseThrow(() -> new AuthenticationException(NO_REGISTERED_MEMBER));
    }
}
