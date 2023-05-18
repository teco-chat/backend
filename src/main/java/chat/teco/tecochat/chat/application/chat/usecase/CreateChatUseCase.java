package chat.teco.tecochat.chat.application.chat.usecase;

import chat.teco.tecochat.chat.domain.chat.Question;

public interface CreateChatUseCase {

    CreateChatResult createChat(CreateChatCommand command);

    record CreateChatCommand(
            Long memberId,
            String message
    ) {
        public Question question() {
            return Question.question(message);
        }
    }

    record CreateChatResult(
            Long chatId,
            String answer
    ) {
    }
}
