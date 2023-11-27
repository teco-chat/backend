package chat.teco.tecochat.query

import chat.teco.tecochat.domain.chat.Chat
import chat.teco.tecochat.domain.chat.QChat.chat
import chat.teco.tecochat.domain.chatlike.QChatLike.chatLike
import chat.teco.tecochat.domain.member.QMember.member
import chat.teco.tecochat.support.domain.BaseEntity
import chat.teco.tecochat.support.querydsl.orderByNotEmpty
import chat.teco.tecochat.support.querydsl.whereNotEmpty
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ChatQueryRepository(
    private val query: JPAQueryFactory,
) {
    fun search(cond: ChatSearchCond, pageable: Pageable): Page<Chat> {
        val longs = nameAndCourseMatchMemberIds(cond)
        val contents = query.selectFrom(chat)
            .leftJoin(chatLike)
            .on(chatLike.chatId.eq(chat.id))
            .distinct()
            .whereNotEmpty(cond.likeCond?.dataCondition()) { chatLike.createdAt.goe(it) }
            .whereNotEmpty(cond.title) { chat.title.likeIgnoreCase("%" + it.trim() + "%") }
            .where(chat.memberId.`in`(longs))
            .orderByNotEmpty(cond.likeCond) { chat.likeCount.desc() }
            .orderBy(chat.createdAt.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetch()
        val countQuery = query.select(chat.count()).from(chat)
        return PageableExecutionUtils.getPage(contents, pageable) { countQuery.fetchOne()!! }
    }

    /* 이름과 코스에 해당하는 회원 id 조회 */
    private fun nameAndCourseMatchMemberIds(cond: ChatSearchCond): List<Long> {
        return query.selectFrom(member)
            .whereNotEmpty(cond.name) { member.name.likeIgnoreCase("%" + it.trim() + "%") }
            .whereNotEmpty(cond.course) { member.course.eq(it) }
            .fetch()
            .map(BaseEntity::id)
    }
}

