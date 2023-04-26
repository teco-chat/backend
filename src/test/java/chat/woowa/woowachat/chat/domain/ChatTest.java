package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.SettingMessage.BACK_END_SETTING;
import static org.assertj.core.api.Assertions.assertThat;

import chat.woowa.woowachat.chat.fixture.ChatFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Chat 은")
class ChatTest {

    @Test
    void 메세지를_추가할_수_있다() {
        // given
        final Chat chat = ChatFixture.chatWithMessages("안녕");

        // when
        chat.addMessage(Message.assistant("응 안녕", 1));

        // then
        assertThat(chat.messages())
                .extracting(Message::content)
                .containsExactly("안녕", "응 안녕");
    }

    @Test
    void 입력받은_토큰보다_메시지의_토큰_총합이_낮도록_오래된_순으로_메시지를_제외한_후_반환한다() {
        // given
        int token = 2000;
        final List<Message> messages = List.of(
                Message.user("Hello1", 1500),
                Message.user("Hello2", 200),
                Message.user("Hello3", 1500)
        );
        final Chat chat = ChatFixture.chatWithMessages("안녕", messages);

        // when
        final List<Message> result = chat.lessThan(token);

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly(BACK_END_SETTING.message(), "Hello2", "Hello3");
    }
}
