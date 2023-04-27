package chat.woowa.woowachat.chat.domain;

import chat.woowa.woowachat.member.domain.Course;
import java.util.EnumMap;
import java.util.Map;

public enum SettingMessage {

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

    private final String message;

    SettingMessage(final String message) {
        this.message = message + DEFAULT_LANGUAGE_SETTING;
    }

    public static SettingMessage byCourse(final Course course) {
        return byCourseMap.get(course);
    }

    public String message() {
        return message;
    }
}
