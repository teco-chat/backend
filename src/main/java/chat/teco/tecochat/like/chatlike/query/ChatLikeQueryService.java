package chat.teco.tecochat.like.chatlike.query;

import static chat.teco.tecochat.chat.domain.chat.QChat.chat;
import static chat.teco.tecochat.like.chatlike.domain.QChatLike.chatLike;
import static chat.teco.tecochat.member.domain.QMember.member;
import static com.querydsl.core.types.Projections.constructor;

import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse.MemberInfo;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikeByMemberIdResponse.ChatInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatLikeQueryService implements
        QueryAllChatLikeByChatIdUseCase,
        QueryAllChatLikedByMemberIdUseCase {

    private final JPAQueryFactory query;

    @Override
    public List<QueryChatLikeByChatIdResponse> findAllByChatId(Long chatId) {
        return query.select(constructor(QueryChatLikeByChatIdResponse.class,
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

    @Override
    public List<QueryChatLikeByMemberIdResponse> findAllByMemberId(Long memberId) {
        return query.select(constructor(QueryChatLikeByMemberIdResponse.class,
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
}
