package chat.teco.tecochat.like.chatlike.application;

import static chat.teco.tecochat.chat.domain.QChat.chat;
import static chat.teco.tecochat.like.chatlike.domain.QChatLike.chatLike;
import static chat.teco.tecochat.member.domain.QMember.member;
import static com.querydsl.core.types.Projections.constructor;

import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByChatIdQueryDto.MemberInfo;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByMemberIdQueryDto.ChatInfo;
import chat.teco.tecochat.member.domain.Course;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ChatLikeQueryService {

    private final JPAQueryFactory query;

    public ChatLikeQueryService(final JPAQueryFactory query) {
        this.query = query;
    }

    public List<ChatLikeByChatIdQueryDto> findAllByChatId(final Long chatId) {
        return query.select(constructor(ChatLikeByChatIdQueryDto.class,
                                chatLike.id,
                                chatLike.createdAt,
                                constructor(MemberInfo.class,
                                        member.id,
                                        member.name,
                                        member.course
                                )
                        )
                ).from(chatLike)
                .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
                .leftJoin(member).on(member.id.eq(chatLike.memberId))
                .where(
                        chatLike.chatId.eq(chatId)
                )
                .orderBy(chatLike.createdAt.desc())
                .fetch();
    }

    public List<ChatLikeByMemberIdQueryDto> findAllByMemberId(final Long memberId) {
        return query.select(
                        constructor(ChatLikeByMemberIdQueryDto.class,
                                chatLike.id,
                                chatLike.createdAt,
                                constructor(ChatInfo.class,
                                        chat.id,
                                        member.id,
                                        member.name,
                                        member.course,
                                        chat.title,
                                        chat.likeCount,
                                        chat.questionAndAnswers.questionAndAnswers.size(),
                                        chat.createdAt
                                )
                        )
                ).from(chatLike)
                .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
                .leftJoin(member).on(member.id.eq(chat.memberId))
                .where(
                        chatLike.memberId.eq(memberId)
                )
                .orderBy(chatLike.createdAt.desc())
                .fetch();
    }

    /**
     * 채팅에 달린 좋아요를 조회할때 반환되는 정보
     */
    public record ChatLikeByChatIdQueryDto(
            Long id,
            LocalDateTime createdAt,
            MemberInfo memberInfo
    ) {

        public record MemberInfo(
                Long id,
                String crewName,
                Course course
        ) {
        }
    }

    /**
     * 회원이 누른 좋아요를 좋아할때 반환되는 정보
     */
    public record ChatLikeByMemberIdQueryDto(
            Long id,
            LocalDateTime createdAt,
            ChatInfo chatInfo
    ) {

        public record ChatInfo(
                Long id,
                Long crewId,
                String crewName,
                Course course,
                String title,
                int likeCount,
                int totalQnaCount,
                LocalDateTime createdAt
        ) {
        }
    }
}
