package chat.woowa.woowachat.member;

import static chat.woowa.woowachat.member.Course.ANDROID;
import static chat.woowa.woowachat.member.Course.BACKEND;
import static chat.woowa.woowachat.member.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Member 은")
class MemberTest {

    @Test
    void 과정별로_정적_팩터리가_존재한다() {
        // when
        final Member 백엔드 = Member.backend("백엔드");
        final Member 프론트 = Member.frontEnd("프론트");
        final Member 안드로이드 = Member.android("안드로이드");

        // then
        assertAll(
                () -> assertThat(백엔드.course()).isEqualTo(BACKEND),
                () -> assertThat(프론트.course()).isEqualTo(FRONTEND),
                () -> assertThat(안드로이드.course()).isEqualTo(ANDROID)
        );
    }
}
