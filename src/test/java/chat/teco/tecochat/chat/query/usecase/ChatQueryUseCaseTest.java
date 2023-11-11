package chat.teco.tecochat.chat.query.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.chat.query.ChatQueryService;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao;
import chat.teco.tecochat.domain.member.MemberRepository;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Member;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

@SuppressWarnings("NonAsciiCharacters")
public class ChatQueryUseCaseTest {

    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final ChatRepository chatRepository = mock(ChatRepository.class);
    protected final ChatQueryDao chatQueryDao = mock(ChatQueryDao.class);
    protected final ChatLikeRepository chatLikeRepository = mock(ChatLikeRepository.class);
    protected final KeywordRepository keywordRepository = mock(KeywordRepository.class);

    protected final ChatQueryService chatQueryService = new ChatQueryService(
            memberRepository, chatRepository, chatQueryDao, chatLikeRepository, keywordRepository
    );

    protected void 회원을_저장한다(Member member) {
        given(memberRepository.findById(member.id()))
                .willReturn(Optional.of(member));
    }

    protected void 채팅을_저장한다(Chat chat) {
        given(chatRepository.findById(1L))
                .willReturn(Optional.of(chat));
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
