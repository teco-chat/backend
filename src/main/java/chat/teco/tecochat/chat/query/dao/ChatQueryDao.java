package chat.teco.tecochat.chat.query.dao;

import static chat.teco.tecochat.domain.chat.QChat.chat;
import static chat.teco.tecochat.domain.chatlike.QChatLike.chatLike;
import static chat.teco.tecochat.domain.member.QMember.member;
import static java.time.DayOfWeek.MONDAY;
import static java.time.LocalTime.MIN;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static org.springframework.util.StringUtils.hasText;

import chat.teco.tecochat.common.query.OrderByNull;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.support.domain.BaseEntity;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ChatQueryDao {

    private final JPAQueryFactory query;

    public ChatQueryDao(JPAQueryFactory query) {
        this.query = query;
    }

    public Page<Chat> search(ChatSearchCond cond, Pageable pageable) {
        List<Long> longs = nameAndCourseMatchMemberIds(cond);
        List<Chat> contents = query.selectFrom(chat)
                .leftJoin(chatLike)
                .on(chatLike.chatId.eq(chat.id))
                .distinct()
                .where(
                        likeCondition(cond.likeCond()),
                        titleLikeIgnoreCase(cond.title),
                        chat.memberId.in(longs)
                )
                .orderBy(
                        likeCountDesc(cond.likeCond),
                        chat.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query.select(chat.count()).from(chat);

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    /* 이름과 코스에 해당하는 회원 id 조회 */
    private List<Long> nameAndCourseMatchMemberIds(ChatSearchCond cond) {
        return query.selectFrom(member)
                .where(
                        nameLikeIgnoreCase(cond.name),
                        courseEquals(cond.course)
                ).fetch()
                .stream()
                .map(BaseEntity::getId)
                .toList();
    }

    private BooleanExpression nameLikeIgnoreCase(String name) {
        return hasText(name) ? member.name.likeIgnoreCase("%" + name.strip() + "%") : null;
    }

    private BooleanExpression courseEquals(Course course) {
        return (course == null) ? null : member.course.eq(course);
    }

    private BooleanExpression titleLikeIgnoreCase(String title) {
        return hasText(title) ? chat.title.likeIgnoreCase("%" + title.strip() + "%") : null;
    }

    private Predicate likeCondition(LikeCond likeCond) {
        return (likeCond == null) ? null : likeCreateAtAfter(likeCond.dataCondition());
    }

    private Predicate likeCreateAtAfter(LocalDateTime localDateTime) {
        return (localDateTime == null) ? null : chatLike.createdAt.goe(localDateTime);
    }

    private OrderSpecifier<?> likeCountDesc(LikeCond likeCond) {
        return (likeCond == null) ? OrderByNull.getDefault() : chat.likeCount.desc();
    }

    public enum LikeCond {
        TODAY(() -> LocalDateTime.now().with(MIN)),
        WEEK(() -> LocalDateTime.now().with(previousOrSame(MONDAY)).with(MIN)),
        MONTH(() -> LocalDateTime.now().withDayOfMonth(1).with(MIN)),
        YEAR(() -> LocalDateTime.now().withDayOfYear(1).with(MIN)),
        ALL(() -> null);

        private final Supplier<LocalDateTime> localDateTimeSupplier;

        LikeCond(Supplier<LocalDateTime> localDateSupplier) {
            this.localDateTimeSupplier = localDateSupplier;
        }

        public LocalDateTime dataCondition() {
            return localDateTimeSupplier.get();
        }
    }

    public record ChatSearchCond(
            String name,
            String title,
            Course course,
            LikeCond likeCond
    ) {
    }
}
