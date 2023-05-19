package chat.teco.tecochat.member.domain;

import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.common.annotation.JpaRepositoryTest;
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
        Member 말랑 = memberRepository.save(new Member("말랑_좋아요", BACKEND));

        // then
        Member member = memberRepository.findById(말랑.id()).get();
        assertAll(
                () -> assertThat(member.course()).isEqualTo(BACKEND),
                () -> assertThat(member.name()).isEqualTo("말랑_좋아요"),
                () -> assertThat(member.id()).isNotNull()
        );
    }
}
