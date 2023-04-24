package chat.woowa.woowachat.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static chat.woowa.woowachat.chat.Role.*;
import static chat.woowa.woowachat.chat.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Message 은")
class MessageTest {

    @Test
    void 역할별로_정적_팩터리가_존재한다() {
        // when
        final Message 유저 = Message.user("유저", 1);
        final Message 시스템 = Message.system("시스템", 1);
        final Message 어시스턴트 = Message.assistant("어시스턴트", 1);

        // then
        assertAll(
                () -> assertThat(유저.role()).isEqualTo(USER),
                () -> assertThat(시스템.role()).isEqualTo(SYSTEM),
                () -> assertThat(어시스턴트.role()).isEqualTo(ASSISTANT)
        );
    }
}
