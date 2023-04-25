package chat.woowa.woowachat.chat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

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

    @Test
    void 입력받은_토큰보다_메시지의_토큰_총합이_낮도록_오래된_순으로_메시지를_제외한_후_반환한다() {
        // given
        int token = 2000;
        final Messages messages = new Messages(
                Message.user("Hello1", 1500),
                Message.user("Hello2", 200),
                Message.user("Hello3", 1500)
        );

        // when
        final List<Message> result = messages.lessThan(token);

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly("Hello2", "Hello3");
    }
}
