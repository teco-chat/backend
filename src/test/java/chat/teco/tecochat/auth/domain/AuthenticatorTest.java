package chat.teco.tecochat.auth.domain;

import static chat.teco.tecochat.auth.exception.AuthenticationExceptionType.NO_REGISTERED_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.auth.exception.AuthenticationException;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Authenticator 은(는)")
class AuthenticatorTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final Authenticator authenticator = new Authenticator(memberRepository);

    @Test
    void Base64로_인코딩된_이름으로_인증() {
        // given
        given(memberRepository.findByName("mallang")).willReturn(Optional.of(말랑.회원()));

        // when
        Long id = authenticator.authenticateWithBase64("bWFsbGFuZw==").id();

        // then
        assertThat(id).isEqualTo(말랑.ID);
    }

    @Test
    void 가입한_회원이_없으면_오류() {
        // given
        given(memberRepository.findByName("mallang")).willReturn(Optional.empty());

        // when
        BaseExceptionType baseExceptionType = assertThrows(AuthenticationException.class, () ->
                authenticator.authenticateWithBase64("bWFsbGFuZw==")
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_REGISTERED_MEMBER);
    }
}
