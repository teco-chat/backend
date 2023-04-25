package chat.woowa.woowachat.chat.fixture;

import static chat.woowa.woowachat.chat.GptModel.GPT_3_5_TURBO;
import static chat.woowa.woowachat.chat.SettingMessage.BACK_END_SETTING;

import chat.woowa.woowachat.chat.Chat;
import chat.woowa.woowachat.chat.Message;
import java.util.List;

public class ChatFixture {

    public static Chat chatWithMessages(final String first, final String... messages) {
        final Chat chat = new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                first,
                1L,
                Message.user(first, 1));

        for (final String message : messages) {
            chat.addMessage(Message.user(message, 1));
        }
        return chat;
    }

    public static Chat chatWithMessages(final String first, final List<Message> messages) {
        final Chat chat = new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                first,
                1L,
                messages.get(0));

        for (Message message : messages.stream().skip(1).toList()) {
            chat.addMessage(message);
        }
        return chat;
    }
}
