package chat.teco.tecochat.query

import chat.teco.tecochat.domain.member.Course
import com.fasterxml.jackson.annotation.JsonCreator
import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime

data class QueryChatLikeByChatIdResponse @QueryProjection constructor(
    val id: Long,
    val createdAt: LocalDateTime,
    val memberInfo: MemberInfo,
)

data class MemberInfo @QueryProjection constructor(
    val id: Long,
    val crewName: String,
    val course: Course,
)

data class QueryChatLikedByMemberIdResponse @JsonCreator constructor(
    val id: Long, val crewId: Long, val crewName: String, val course: Course,
    val title: String, val likeCount: Int, val commentCount: Int, val totalQnaCount: Int,
    val keywords: MutableList<QueryLikedChatKeywordDto>, val createdAt: LocalDateTime,
) {

    @QueryProjection
    constructor(
        id: Long, crewId: Long, crewName: String, course: Course, title: String,
        likeCount: Int, commentCount: Int, totalQnaCount: Int, createdAt: LocalDateTime,
    ) : this(
        id = id, crewId = crewId, crewName = crewName, course = course, title = title,
        likeCount = likeCount, commentCount = commentCount, totalQnaCount = totalQnaCount,
        keywords = ArrayList<QueryLikedChatKeywordDto>(), createdAt = createdAt
    )

    fun addKeywords(keywords: List<QueryLikedChatKeywordDto>) {
        this.keywords.addAll(keywords)
    }
}

data class QueryLikedChatKeywordDto @JsonCreator @QueryProjection constructor(
    val keyword: String,
)
