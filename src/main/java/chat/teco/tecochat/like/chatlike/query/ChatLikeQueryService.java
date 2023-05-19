package chat.teco.tecochat.like.chatlike.query;

import static chat.teco.tecochat.chat.domain.chat.QChat.chat;
import static chat.teco.tecochat.chat.domain.keyword.QKeyword.keyword1;
import static chat.teco.tecochat.like.chatlike.domain.QChatLike.chatLike;
import static chat.teco.tecochat.member.domain.QMember.member;
import static com.querydsl.core.types.Projections.constructor;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse.MemberInfo;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikedByMemberIdResponse.QueryLikedChatKeywordDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
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
                                member.id, member.name, member.course
                        )
                )).from(chatLike)
                .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
                .leftJoin(member).on(member.id.eq(chatLike.memberId))
                .where(chatLike.chatId.eq(chatId))
                .orderBy(chatLike.createdAt.desc())
                .fetch();
    }

    @Override
    public Page<QueryChatLikedByMemberIdResponse> findAllByMemberId(Long memberId, Pageable pageable) {
        List<QueryChatLikedByMemberIdResponse> chatResponses = query.select(
                        constructor(QueryChatLikedByMemberIdResponse.class,
                                chat.id,
                                member.id, member.name, member.course,
                                chat.title, chat.likeCount,
                                chat.questionAndAnswers.questionAndAnswers.size(),
                                chat.createdAt
                        )
                ).from(chatLike)
                .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
                .leftJoin(member).on(member.id.eq(chat.memberId))
                .where(chatLike.memberId.eq(memberId))
                .orderBy(chatLike.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        settingKeywords(chatResponses);

        JPAQuery<Long> countQuery = query.select(chat.count())
                .from(chatLike)
                .leftJoin(chat).on(chat.id.eq(chatLike.chatId))
                .leftJoin(member).on(member.id.eq(chat.memberId))
                .where(chatLike.memberId.eq(memberId));
        return PageableExecutionUtils.getPage(chatResponses, pageable, countQuery::fetchOne);
    }

    private void settingKeywords(List<QueryChatLikedByMemberIdResponse> chatResponses) {
        Map<Long, List<Keyword>> chatIdKeywordMap = query.selectFrom(keyword1)
                .join(keyword1.chat, chat).fetchJoin()
                .where(keyword1.chat.id.in(chatIds(chatResponses)))
                .fetch()
                .stream()
                .collect(groupingBy(keyword -> keyword.chat().id()));

        for (QueryChatLikedByMemberIdResponse chatResponse : chatResponses) {
            List<QueryLikedChatKeywordDto> list = chatIdKeywordMap.getOrDefault(chatResponse.id(), emptyList())
                    .stream()
                    .map(it -> new QueryLikedChatKeywordDto(it.keyword()))
                    .toList();
            chatResponse.addKeywords(list);
        }
    }

    private List<Long> chatIds(List<QueryChatLikedByMemberIdResponse> chatResponses) {
        return chatResponses.stream()
                .map(QueryChatLikedByMemberIdResponse::id)
                .toList();
    }
}
