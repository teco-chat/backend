package chat.woowa.woowachat.member;

import static chat.woowa.woowachat.member.Course.BACKEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.woowa.woowachat.common.annotation.JpaRepositoryTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("MemberRepository 는")
@JpaRepositoryTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void Member_를_저장할_수_있다() {
        // when
        final Member 말랑 = memberRepository.save(Member.backend("말랑"));

        // then
        final Member member = memberRepository.findById(말랑.id()).get();
        assertAll(
                () -> assertThat(member.course()).isEqualTo(BACKEND),
                () -> assertThat(member.name()).isEqualTo("말랑"),
                () -> assertThat(member.id()).isNotNull()
        );
    }
}
