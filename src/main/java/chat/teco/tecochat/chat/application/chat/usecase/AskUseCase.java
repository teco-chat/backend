package chat.teco.tecochat.chat.application.chat.usecase;

import chat.teco.tecochat.chat.domain.chat.Question;

public interface AskUseCase {

    AskResult ask(Long id, AskCommand command);

    record AskCommand(
            Long memberId,
            String message
    ) {
        public Question question() {
            return Question.question(message);
        }
    }

    record AskResult(
            String answer
    ) {
    }
}
