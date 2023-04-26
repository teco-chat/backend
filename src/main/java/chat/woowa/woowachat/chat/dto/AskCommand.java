package chat.woowa.woowachat.chat.dto;

public record AskCommand(
        Long memberId,
        String message,
        int token
) {
}
