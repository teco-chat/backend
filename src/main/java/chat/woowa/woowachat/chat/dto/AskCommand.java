package chat.woowa.woowachat.chat.dto;

import chat.woowa.woowachat.chat.domain.Question;

public record AskCommand(
        Long memberId,
        String message
) {
    public Question question() {
        return Question.question(message);
    }
}
