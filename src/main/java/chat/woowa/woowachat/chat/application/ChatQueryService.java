package chat.woowa.woowachat.chat.application;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.dto.ChatQueryDto;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class ChatQueryService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;

    public ChatQueryService(final MemberRepository memberRepository, final ChatRepository chatRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
    }

    public ChatQueryDto findById(final Long id) {
        final Chat chat = chatRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 %d인 채팅이 없습니다."));
        final Member member = memberRepository.findById(chat.memberId())
                .orElseThrow(() -> new IllegalArgumentException("아이디가 %d인 회원 없습니다."));
        return ChatQueryDto.of(chat, member.name());
    }
}
