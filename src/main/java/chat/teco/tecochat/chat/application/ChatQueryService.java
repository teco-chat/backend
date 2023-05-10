package chat.teco.tecochat.chat.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatQueryRepository;
import chat.teco.tecochat.chat.domain.ChatQueryRepository.ChatSearchCond;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.dto.ChatQueryDto;
import chat.teco.tecochat.chat.dto.ChatSearchQueryDto;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
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

    public ChatQueryService(final MemberRepository memberRepository,
                            final ChatRepository chatRepository,
                            final ChatQueryRepository chatQueryRepository,
                            final ChatLikeRepository chatLikeRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.chatQueryRepository = chatQueryRepository;
        this.chatLikeRepository = chatLikeRepository;
    }

    public ChatQueryDto findById(final Long id) {
        final Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
        final Member member = findMemberById(chat.memberId());
        final boolean isAlreadyClickLike = chatLikeRepository
                .findByMemberIdAndChatId(member.id(), id)
                .isPresent();
        return ChatQueryDto.of(chat, member.name(), member.course(), isAlreadyClickLike);
    }

    public Page<ChatSearchQueryDto> search(final ChatSearchCond cond, final Pageable pageable) {
        return chatQueryRepository.search(cond, pageable)
                .map(it -> ChatSearchQueryDto.from(it, findMemberById(it.memberId())));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
