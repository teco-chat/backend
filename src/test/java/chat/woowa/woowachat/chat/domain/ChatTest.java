package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.Chat.FREE_TOKEN;
import static chat.woowa.woowachat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.woowa.woowachat.chat.domain.SettingMessage.BACK_END_SETTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chat.woowa.woowachat.chat.exception.TokenSizeBigException;
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
    void 생성_시_최대토큰에서_가용토큰을_뺀_길이만큼의_메세지가_들어오면_예외() {
        // when & then
        assertThatThrownBy(() -> new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                "title",
                1L,
                Message.user("max", GPT_3_5_TURBO.maxTokens() - FREE_TOKEN + 1))
        ).isInstanceOf(TokenSizeBigException.class);
    }

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
    void 메세지를_추가_시_최대토큰에서_가용토큰을_뺀_길이만큼의_메세지가_들어오면_예외() {
        // given
        final List<Message> messages = List.of(Message.user("Hello1", 1));
        final Chat chat = ChatFixture.chatWithModelAndMessages(GPT_3_5_TURBO, messages);

        // when & then
        assertThatThrownBy(() ->
                chat.addMessage(Message.user("max", GPT_3_5_TURBO.maxTokens() - FREE_TOKEN + 1))
        ).isInstanceOf(TokenSizeBigException.class);
    }


    @Test
    void 주어진_가용_토큰만큼의_공간_이상을_확보하도록_오래된_순으로_메시지를_제외한_후_세팅_메세지를_포함하여_반환한다() {
        // given
        final List<Message> messages = List.of(
                Message.user("Hello1", 1500),
                Message.user("Hello2", 200),
                Message.user("Hello3", 1500)
        );
        final Chat chat = ChatFixture.chatWithMessages(messages);

        // when
        final List<Message> result = chat.messagesWithFreeToken();

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly(BACK_END_SETTING.message(), "Hello2", "Hello3");
    }

    @Test
    void 주어진_가용_토큰만큼의_공간_이상을_확보하도록_오래된_순으로_메시지를_제외한_후_세팅_메세지를_포함하여_반환한다_엣지_케이스() {
        // given
        final List<Message> messages = List.of(
                Message.user("Hello1", 1500),
                Message.user("Hello2", 200),
                Message.user("Hello3", GPT_3_5_TURBO.maxTokens() - FREE_TOKEN)
        );
        final Chat chat = ChatFixture.chatWithMessages(messages);

        // when
        final List<Message> result = chat.messagesWithFreeToken();

        // then
        assertThat(result).extracting(Message::content)
                .containsExactly(BACK_END_SETTING.message(), "Hello3");
    }
}
