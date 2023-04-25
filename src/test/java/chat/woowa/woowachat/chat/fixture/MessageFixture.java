package chat.woowa.woowachat.chat.fixture;

import chat.woowa.woowachat.chat.Message;
import chat.woowa.woowachat.chat.Messages;
import java.util.Arrays;
import java.util.List;

public class MessageFixture {

    public static Messages messages(final String... content) {
        final List<Message> messages = Arrays.stream(content)
                .map(it -> Message.user(it, 1))
                .toList();
        return new Messages(messages);
    }
}
