package chat.woowa.woowachat.chat;

public enum Role {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    ;

    private final String roleName;

    Role(final String roleName) {
        this.roleName = roleName;
    }

    public String roleName() {
        return roleName;
    }
}
