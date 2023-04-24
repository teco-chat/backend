package chat.woowa.woowachat.chat;

import chat.woowa.woowachat.chat.fixture.ChatFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
