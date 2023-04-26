package chat.woowa.woowachat.chat.fixture;

import static chat.woowa.woowachat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.woowa.woowachat.chat.domain.SettingMessage.BACK_END_SETTING;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.GptModel;
import chat.woowa.woowachat.chat.domain.Message;
import java.util.Arrays;
import java.util.List;

public class ChatFixture {

    public static Chat chatWithMessages(final String... messages) {
        final Chat chat = new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                messages[0],
                1L,
                Message.user(messages[0], 1));

        for (final String message : Arrays.stream(messages).skip(1).toList()) {
            chat.addMessage(Message.user(message, 1));
        }
        return chat;
    }

    public static Chat chatWithMessages(final List<Message> messages) {
        return chatWithModelAndMessages(GPT_3_5_TURBO, messages);
    }

    public static Chat chatWithModelAndMessages(final GptModel gptModel, final List<Message> messages) {
        final Chat chat = new Chat(gptModel,
                BACK_END_SETTING,
                messages.get(0).content(),
                1L,
                messages.get(0));

        for (Message message : messages.stream().skip(1).toList()) {
            chat.addMessage(message);
        }
        return chat;
    }
}
