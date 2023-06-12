package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.Role.SYSTEM;

import chat.teco.tecochat.member.domain.Course;
import java.util.EnumMap;
import java.util.Map;

public enum SettingMessage implements Message {

    // (참고) 토큰 수는 31 나옴
    BACK_END_SETTING("You are a helpful backend developer assistant."),
    FRONT_END_SETTING("You are a helpful frontend developer assistant."),
    ANDROID_SETTING("You are a helpful android developer assistant."),
    ;

    private static final String DEFAULT_LANGUAGE_SETTING = "If there is no request, please reply in Korean.";
    private static final Map<Course, SettingMessage> byCourseMap;

    static {
        byCourseMap = new EnumMap<>(Course.class);
        byCourseMap.put(Course.BACKEND, BACK_END_SETTING);
        byCourseMap.put(Course.FRONTEND, FRONT_END_SETTING);
        byCourseMap.put(Course.ANDROID, ANDROID_SETTING);
    }

    private final String content;

    SettingMessage(String message) {
        this.content = message + DEFAULT_LANGUAGE_SETTING;
    }

    public static SettingMessage byCourse(Course course) {
        return byCourseMap.get(course);
    }

    public String message() {
        return content;
    }

    @Override
    public String roleName() {
        return SYSTEM.roleName();
    }

    @Override
    public String content() {
        return content;
    }
}
