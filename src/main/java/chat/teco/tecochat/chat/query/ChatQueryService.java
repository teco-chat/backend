package chat.teco.tecochat.chat.query;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.chat.query.mapper.ChatMapper.mapToQueryResponse;
import static chat.teco.tecochat.chat.query.mapper.ChatMapper.mapToSearchQueryResponse;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.ChatSearchCond;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase;
import chat.teco.tecochat.common.entity.BaseEntity;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatQueryService implements QueryChatByIdUseCase, SearchChatUseCase {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatQueryDao chatQueryDao;
    private final ChatLikeRepository chatLikeRepository;
    private final KeywordRepository keywordRepository;

    @Override
    public QueryChatByIdResponse findById(Long id, Long requesterMemberId) {
        Chat chat = findChatById(id);
        Member member = findMemberById(chat.memberId());
        boolean isAlreadyClickLike = chatLikeRepository
                .findByMemberIdAndChatId(requesterMemberId, id)
                .isPresent();
        List<Keyword> keywords = keywordRepository.findAllByChatId(id);
        return mapToQueryResponse(chat, member, isAlreadyClickLike, keywords);
    }

    private Chat findChatById(Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    @Override
    public Page<SearchChatResponse> search(ChatSearchCond cond, Pageable pageable) {
        Page<Chat> result = chatQueryDao.search(cond, pageable);
        List<Long> chatIds = result.stream().map(BaseEntity::id).toList();
        Map<Long, List<Keyword>> chatIdKeywordMapping = findAllByChatIds(chatIds);
        return result.map(chat -> mapToSearchQueryResponse(
                chat,
                findMemberById(chat.memberId()),
                chatIdKeywordMapping.getOrDefault(chat.id(), emptyList())));
    }

    private Map<Long, List<Keyword>> findAllByChatIds(List<Long> chatIds) {
        return keywordRepository.findAllInChatIds(chatIds)
                .stream()
                .collect(groupingBy(keyword -> keyword.chat().id()));
    }
}
