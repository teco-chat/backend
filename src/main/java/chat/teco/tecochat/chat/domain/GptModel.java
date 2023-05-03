package chat.teco.tecochat.chat.domain;

public enum GptModel {

    GPT_3_5_TURBO("gpt-3.5-turbo", 4096),
    GPT_4("gpt-4", 8192),
    GPT_4_32K("gpt-4-32k", 32768),
    ;

    private final String modelName;
    private final int maxTokens;

    GptModel(final String modelName, final int maxTokens) {
        this.modelName = modelName;
        this.maxTokens = maxTokens;
    }

    public String modelName() {
        return modelName;
    }

    public int maxTokens() {
        return maxTokens;
    }
}
