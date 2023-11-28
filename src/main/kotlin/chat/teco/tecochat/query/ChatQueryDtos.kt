package chat.teco.tecochat.query

import chat.teco.tecochat.domain.member.Course
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters
import java.util.function.Supplier

enum class LikeCond(
    private val localDateTimeSupplier: Supplier<LocalDateTime?>,
) {
    TODAY(Supplier { LocalDateTime.now().with(LocalTime.MIN) }),
    WEEK(Supplier {
        LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).with(LocalTime.MIN)
    }),
    MONTH(Supplier { LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN) }),
    YEAR(Supplier { LocalDateTime.now().withDayOfYear(1).with(LocalTime.MIN) }),
    ALL(Supplier { null });

    fun dataCondition(): LocalDateTime? {
        return localDateTimeSupplier.get()
    }
}

data class ChatSearchCond(
    val name: String?,
    val title: String?,
    val course: Course?,
    val likeCond: LikeCond?,
)

data class QueryMessageDto(
    val content: String,
    val role: String,
    val createdAt: LocalDateTime,
)

data class QueryKeywordDto @JsonCreator constructor(
    val keyword: String,
)

data class ChatResponse(
    val id: Long,
    val crewName: String,
    val course: Course,
    val title: String,
    val likeCount: Int,
    @get:JsonProperty("isAlreadyClickLike")
    val isAlreadyClickLike: Boolean,
    val createdAt: LocalDateTime,
    val messages: List<QueryMessageDto>,
    val keywords: List<QueryKeywordDto>,
)

data class SearchKeywordDto @JsonCreator constructor(
    val keyword: String,
)

data class SearchChatResponse(
    val id: Long,
    val crewId: Long,
    val crewName: String,
    val course: Course,
    val title: String,
    val likeCount: Int,
    val commentCount: Int,
    val totalQnaCount: Int,
    val keywords: List<SearchKeywordDto>,
    val createdAt: LocalDateTime,
)
