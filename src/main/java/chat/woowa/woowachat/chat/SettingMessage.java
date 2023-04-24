package chat.woowa.woowachat.chat;

public enum SettingMessage {

    BACK_END_SETTING("You are a helpful backend developer assistant"),
    FRONT_END_SETTING("You are a helpful frontend developer assistant"),
    ANDROID_SETTING("You are a helpful android developer assistant"),
    ;

    private final String message;

    SettingMessage(final String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
