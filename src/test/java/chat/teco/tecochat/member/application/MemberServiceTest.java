package chat.teco.tecochat.member.application;

import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.dto.SignUpDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("MemberService[회원가입] 은")
@Transactional
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberService memberService;

    @Test
    void 이름으로_회원가입을_진행한다() {
        // given
        final SignUpDto 말랑 = new SignUpDto("말랑", BACKEND);

        // when
        memberService.signUp(말랑);

        // then
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    void 이미_이름이_존재하는_경우_코스를_변경한다() {
        // given
        final SignUpDto before = new SignUpDto("말랑", BACKEND);
        final SignUpDto after = new SignUpDto("말랑", FRONTEND);
        memberService.signUp(before);

        // when
        memberService.signUp(after);

        // then
        assertAll(
                () ->
                        assertThat(memberRepository.findAll()).hasSize(1),
                () ->
                        assertThat(memberRepository.findByName("말랑").get().course())
                                .isEqualTo(FRONTEND)
        );
    }
}
