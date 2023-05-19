package chat.teco.tecochat.chat.application.chat.usecase;

public interface UpdateChatTitleUseCase {

    void updateTitle(UpdateChatTitleCommand command);

    record UpdateChatTitleCommand(
            Long memberId,
            Long chatId,
            String title
    ) {
    }
}
