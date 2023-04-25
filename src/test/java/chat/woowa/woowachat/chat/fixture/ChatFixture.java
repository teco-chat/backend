package chat.woowa.woowachat.chat.fixture;

import chat.woowa.woowachat.chat.Chat;
import chat.woowa.woowachat.chat.Message;

import static chat.woowa.woowachat.chat.GptModel.GPT_3_5_TURBO;
import static chat.woowa.woowachat.chat.SettingMessage.BACK_END_SETTING;

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
}
