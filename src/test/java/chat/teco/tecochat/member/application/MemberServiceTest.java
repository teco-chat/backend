package chat.teco.tecochat.member.application;

import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.member.application.dto.SignUpCommand;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("MemberService 은(는)")
class MemberServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final MemberService memberService = new MemberService(memberRepository);

    @Test
    void 이름으로_회원가입을_진행한다() {
        // given
        회원가입_진행되지_않음();
        SignUpCommand command = 말랑.회원가입_명령;

        // when
        memberService.signUp(command);

        // then
        then(memberRepository)
                .should(times(1))
                .save(any(Member.class));
    }

    @Test
    void 이미_이름이_존재하는_경우_코스를_변경한다() {
        // given
        Member 말랑이 = 말랑.회원();
        회원가입_진행(말랑이);

        // when
        memberService.signUp(new SignUpCommand(말랑.이름, FRONTEND));

        // then
        assertAll(
                () -> assertThat(말랑이.course()).isEqualTo(FRONTEND),
                () -> then(memberRepository)
                        .should(times(0))
                        .save(any(Member.class))
        );
    }

    private void 회원가입_진행되지_않음() {
        given(memberRepository.findById(any())).willReturn(Optional.empty());
    }

    private void 회원가입_진행(Member member) {
        given(memberRepository.findById(member.id())).willReturn(Optional.of(member));
        given(memberRepository.findByName(member.name())).willReturn(Optional.of(member));
    }
}
