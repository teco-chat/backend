package chat.teco.tecochat.domain.chat

import chat.teco.tecochat.domain.member.Course
import java.util.EnumMap

private const val DEFAULT_LANGUAGE_SETTING = "If there is no request, please reply in Korean."

enum class SettingMessage(message: String) : Message {
    // (참고) 토큰 수는 31 나옴
    BACK_END_SETTING("You are a helpful backend developer assistant."),
    FRONT_END_SETTING("You are a helpful frontend developer assistant."),
    ANDROID_SETTING("You are a helpful android developer assistant.");

    private val content: String

    init {
        content = message + DEFAULT_LANGUAGE_SETTING
    }

    fun message(): String {
        return content
    }

    override fun roleName(): String {
        return Role.SYSTEM.roleName
    }

    override fun content(): String {
        return content
    }

    companion object {
        private val byCourseMap: MutableMap<Course, SettingMessage>

        init {
            byCourseMap = EnumMap(Course::class.java)
            byCourseMap[Course.BACKEND] = BACK_END_SETTING
            byCourseMap[Course.FRONTEND] = FRONT_END_SETTING
            byCourseMap[Course.ANDROID] = ANDROID_SETTING
        }

        @JvmStatic
        fun byCourse(course: Course): SettingMessage {
            return byCourseMap[course] ?: BACK_END_SETTING
        }
    }
}
