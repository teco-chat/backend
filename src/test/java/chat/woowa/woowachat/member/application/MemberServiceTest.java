package chat.woowa.woowachat.member.application;

import static org.assertj.core.api.Assertions.assertThat;

import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.MemberRepository;
import chat.woowa.woowachat.member.dto.SignUpDto;
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
        final SignUpDto 말랑 = new SignUpDto("말랑", Course.BACKEND);

        // when
        memberService.signUp(말랑);

        // then
        assertThat(memberRepository.findAll()).hasSize(1);
    }

    @Test
    void 이미_이름이_존재하는_경우_따로_저장하지_않는다() {
        // given
        이름으로_회원가입을_진행한다();

        // when
        이름으로_회원가입을_진행한다();

        // then
        assertThat(memberRepository.findAll()).hasSize(1);
    }
}
