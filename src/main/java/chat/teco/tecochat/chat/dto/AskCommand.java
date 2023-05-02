package chat.teco.tecochat.chat.dto;

import chat.teco.tecochat.chat.domain.Question;

public record AskCommand(
        Long memberId,
        String message
) {
    public Question question() {
        return Question.question(message);
    }
}
