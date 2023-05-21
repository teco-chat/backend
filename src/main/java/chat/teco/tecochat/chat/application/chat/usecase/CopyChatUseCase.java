package chat.teco.tecochat.chat.application.chat.usecase;

public interface CopyChatUseCase {

    Long copy(CopyCommand command);

    record CopyCommand(
            Long chatId,
            Long memberId
    ) {
    }
}
