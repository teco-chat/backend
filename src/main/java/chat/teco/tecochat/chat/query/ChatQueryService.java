package chat.teco.tecochat.chat.query;

import static chat.teco.tecochat.chat.query.mapper.ChatMapper.mapToQueryResponse;
import static chat.teco.tecochat.chat.query.mapper.ChatMapper.mapToSearchQueryResponse;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.ChatSearchCond;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase;
import chat.teco.tecochat.common.entity.BaseEntity;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chat.KeywordRepository;
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository;
import chat.teco.tecochat.domain.member.Member;
import chat.teco.tecochat.domain.member.MemberRepository;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ChatQueryService implements QueryChatByIdUseCase, SearchChatUseCase {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatQueryDao chatQueryDao;
    private final ChatLikeRepository chatLikeRepository;
    private final KeywordRepository keywordRepository;

    public ChatQueryService(MemberRepository memberRepository, ChatRepository chatRepository, ChatQueryDao chatQueryDao,
                            ChatLikeRepository chatLikeRepository, KeywordRepository keywordRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.chatQueryDao = chatQueryDao;
        this.chatLikeRepository = chatLikeRepository;
        this.keywordRepository = keywordRepository;
    }

    @Override
    public QueryChatByIdResponse findById(Long id, Long requesterMemberId) {
        Chat chat = chatRepository.getById(id);
        Member member = memberRepository.getById(chat.memberId());
        boolean isAlreadyClickLike = chatLikeRepository.findByMemberIdAndChatId(requesterMemberId, id) != null;
        List<Keyword> keywords = keywordRepository.findAllByChatId(id);
        return mapToQueryResponse(chat, member, isAlreadyClickLike, keywords);
    }

    @Override
    public Page<SearchChatResponse> search(ChatSearchCond cond, Pageable pageable) {
        Page<Chat> result = chatQueryDao.search(cond, pageable);
        List<Long> chatIds = result.stream().map(BaseEntity::id).toList();
        Map<Long, List<Keyword>> chatIdKeywordMapping = findAllByChatIds(chatIds);
        return result.map(chat -> mapToSearchQueryResponse(
                chat,
                memberRepository.getById(chat.memberId()),
                chatIdKeywordMapping.getOrDefault(chat.id(), emptyList())));
    }

    private Map<Long, List<Keyword>> findAllByChatIds(List<Long> chatIds) {
        return keywordRepository.findAllInChatIds(chatIds)
                .stream()
                .collect(groupingBy(keyword -> keyword.chat().id()));
    }
}
