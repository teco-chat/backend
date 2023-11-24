package chat.teco.tecochat.domain.chatlike

import chat.teco.tecochat.domain.chat.QChat.chat
import chat.teco.tecochat.domain.chatlike.QChatLike.chatLike
import chat.teco.tecochat.domain.keyword.QKeyword.keyword1
import chat.teco.tecochat.domain.member.Course
import chat.teco.tecochat.domain.member.QMember.member
import com.fasterxml.jackson.annotation.JsonCreator
import com.querydsl.core.annotations.QueryProjection
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.stream.Collectors

@Component
class ChatLikeQueryRepository(
    val query: JPAQueryFactory,
) {

    fun findAllByChatId(chatId: Long): List<QueryChatLikeByChatIdResponse> {
        return query.select(
            QQueryChatLikeByChatIdResponse(
                chatLike.id,
                chatLike.createdAt,
                QMemberInfo(
                    member.id,
                    member.name,
                    member.course
                )
            )
        ).from(chatLike)
            .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
            .leftJoin(member).on(member.id.eq(chatLike.memberId))
            .where(chatLike.chatId.eq(chatId))
            .orderBy(chatLike.createdAt.desc())
            .fetch()
    }

    fun findAllByMemberId(
        memberId: Long, pageable: Pageable,
    ): Page<QueryChatLikedByMemberIdResponse> {
        val chatResponses = query.select(
            QQueryChatLikedByMemberIdResponse(
                chat.id, member.id, member.name, member.course,
                chat.title, chat.likeCount, chat.commentCount,
                chat.questionAndAnswers.questionAndAnswers.size(), chat.createdAt
            )
        ).from(chatLike)
            .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
            .leftJoin(member).on(member.id.eq(chat.memberId))
            .where(chatLike.memberId.eq(memberId))
            .orderBy(chatLike.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()

        settingKeywords(chatResponses)

        val countQuery = query.select(chat.count())
            .from(chatLike)
            .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
            .leftJoin(member).on(member.id.eq(chat.memberId))
            .where(chatLike.memberId.eq(memberId))

        return PageableExecutionUtils.getPage(chatResponses, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun settingKeywords(chatResponses: List<QueryChatLikedByMemberIdResponse>) {
        val chatIds = chatResponses.map(QueryChatLikedByMemberIdResponse::id)

        val chatIdKeywordMap = query.selectFrom(keyword1)
            .join(keyword1.chat, chat).fetchJoin()
            .where(keyword1.chat.id.`in`(chatIds))
            .fetch()
            .stream()
            .collect(Collectors.groupingBy { keyword -> keyword.chat.id })

        for (chatResponse in chatResponses) {
            val list = chatIdKeywordMap.getOrDefault(chatResponse.id, emptyList())
                .map { QueryLikedChatKeywordDto(it.keyword) }
            chatResponse.addKeywords(list)
        }
    }
}

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

