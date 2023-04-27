package chat.woowa.woowachat.chat.application;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatQueryRepository;
import chat.woowa.woowachat.chat.domain.ChatQueryRepository.ChatSearchCond;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.dto.ChatQueryDto;
import chat.woowa.woowachat.chat.dto.ChatSearchQueryDto;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
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

    public ChatQueryService(final MemberRepository memberRepository,
                            final ChatRepository chatRepository,
                            final ChatQueryRepository chatQueryRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.chatQueryRepository = chatQueryRepository;
    }

    public ChatQueryDto findById(final Long id) {
        final Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 %d인 채팅이 없습니다."));
        final Member member = findMemberById(chat.memberId());
        return ChatQueryDto.of(chat, member.name(), member.course());
    }

    public Page<ChatSearchQueryDto> search(final ChatSearchCond cond, final Pageable pageable) {
        return chatQueryRepository.search(cond, pageable)
                .map(it -> ChatSearchQueryDto.from(it, findMemberById(it.memberId())));
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 %d인 회원 없습니다."));
    }
}
