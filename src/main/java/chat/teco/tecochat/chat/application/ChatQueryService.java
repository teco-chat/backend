package chat.teco.tecochat.chat.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.groupingBy;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatQueryRepository;
import chat.teco.tecochat.chat.domain.ChatQueryRepository.ChatSearchCond;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.chat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.chat.dto.ChatQueryDto;
import chat.teco.tecochat.chat.dto.ChatSearchQueryDto;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.common.entity.BaseEntity;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ChatQueryService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final ChatQueryRepository chatQueryRepository;
    private final ChatLikeRepository chatLikeRepository;
    private final KeywordRepository keywordRepository;

    public ChatQueryService(final MemberRepository memberRepository,
                            final ChatRepository chatRepository,
                            final ChatQueryRepository chatQueryRepository,
                            final ChatLikeRepository chatLikeRepository,
                            final KeywordRepository keywordRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.chatQueryRepository = chatQueryRepository;
        this.chatLikeRepository = chatLikeRepository;
        this.keywordRepository = keywordRepository;
    }

    public ChatQueryDto findById(final Long id, final Long requesterMemberId) {
        final Chat chat = findChatById(id);
        final Member member = findMemberById(chat.memberId());
        final boolean isAlreadyClickLike = chatLikeRepository
                .findByMemberIdAndChatId(requesterMemberId, id)
                .isPresent();
        final List<Keyword> keywords = keywordRepository.findAllByChatId(id);
        return ChatQueryDto.of(chat, member.name(), member.course(), isAlreadyClickLike, keywords);
    }

    private Chat findChatById(final Long id) {
        return chatRepository.findById(id)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    public Page<ChatSearchQueryDto> search(final ChatSearchCond cond, final Pageable pageable) {
        final Page<Chat> result = chatQueryRepository.search(cond, pageable);
        final List<Long> chatIds = result.stream().map(BaseEntity::id).toList();
        final Map<Long, List<Keyword>> chatIdKeywordMapping = findAllByChatIds(chatIds);
        return result.map(it ->
                ChatSearchQueryDto.of(
                        it,
                        findMemberById(it.memberId()),
                        chatIdKeywordMapping.getOrDefault(it.id(), emptyList())));
    }

    private Map<Long, List<Keyword>> findAllByChatIds(final List<Long> chatIds) {
        return keywordRepository.findAllInChatIds(chatIds)
                .stream()
                .collect(groupingBy(it -> it.chat().id()));
    }

    private List<Keyword> findKeywordsByChatId(final Long chatId) {
        return keywordRepository.findAllByChatId(chatId);
    }
}
