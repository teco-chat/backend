package chat.woowa.woowachat.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Messages 는")
class MessagesTest {

    @Test
    void 메세지를_저장할_수_있다() {
        // given
        Messages messages = new Messages();

        // when
        messages.add(Message.user("안녕", 1));

        // then
        assertThat(messages.messages()).hasSize(1);
    }
}
