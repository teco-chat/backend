package chat.woowa.woowachat.chat.domain;

public enum SettingMessage {

    // (참고) 토큰 수는 31 나옴
    BACK_END_SETTING("You are a helpful backend developer assistant."),
    FRONT_END_SETTING("You are a helpful frontend developer assistant."),
    ANDROID_SETTING("You are a helpful android developer assistant."),
    ;

    private static final String DEFAULT_LANGUAGE_SETTING = "If there is no request, please reply in Korean.";

    private final String message;

    SettingMessage(final String message) {
        this.message = message + DEFAULT_LANGUAGE_SETTING;
    }

    public String message() {
        return message;
    }
}
