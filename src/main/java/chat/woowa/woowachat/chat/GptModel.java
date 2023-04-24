package chat.woowa.woowachat.chat;

public enum GptModel {

    GPT_3_5_TURBO(4096),
    GPT_4(8192),
    GPT_4_32K(32768),
    ;

    private final int maxTokens;

    GptModel(final int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public int maxTokens() {
        return maxTokens;
    }
}
