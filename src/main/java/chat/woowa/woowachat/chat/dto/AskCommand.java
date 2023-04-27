package chat.woowa.woowachat.chat.dto;

import chat.woowa.woowachat.chat.domain.Message;

public record AskCommand(
        Long memberId,
        String message,
        int token
) {
    public Message toMessage() {
        return Message.user(message, token);
    }
}
