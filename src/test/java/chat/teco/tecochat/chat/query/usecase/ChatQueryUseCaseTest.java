package chat.teco.tecochat.chat.query.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import chat.teco.tecochat.application.ChatQueryService;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository;
import chat.teco.tecochat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.domain.member.Member;
import chat.teco.tecochat.domain.member.MemberRepository;
import chat.teco.tecochat.query.ChatQueryRepository;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

@SuppressWarnings("NonAsciiCharacters")
public class ChatQueryUseCaseTest {

    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final ChatRepository chatRepository = mock(ChatRepository.class);
    protected final ChatQueryRepository chatQueryDao = mock(ChatQueryRepository.class);
    protected final ChatLikeRepository chatLikeRepository = mock(ChatLikeRepository.class);
    protected final KeywordRepository keywordRepository = mock(KeywordRepository.class);

    protected final ChatQueryService chatQueryService = new ChatQueryService(
            memberRepository, chatRepository, chatQueryDao, chatLikeRepository, keywordRepository
    );

    protected void 회원을_저장한다(Member member) {
        given(memberRepository.getById(member.getId()))
                .willReturn(member);
    }

    protected void 채팅을_저장한다(Chat chat) {
        given(chatRepository.getById(1L))
                .willReturn(chat);
    }

    protected void 페이징_조회_결과_설정(Chat... chats) {
        Page<Chat> page = PageableExecutionUtils.getPage(Arrays.asList(chats),
                PageRequest.of(0, 10),
                () -> 0);
        given(chatQueryDao.search(any(), any()))
                .willReturn(page);
    }

    @AfterEach
    void tearDown() {
        reset(memberRepository, chatRepository, chatQueryDao, chatRepository, keywordRepository);
    }
}
