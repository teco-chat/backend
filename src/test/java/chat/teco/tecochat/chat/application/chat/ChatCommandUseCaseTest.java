package chat.teco.tecochat.chat.application.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.reset;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.application.chat.service.ChatService;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.keyword.GptClient;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;

@SuppressWarnings("NonAsciiCharacters")
public class ChatCommandUseCaseTest {

    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final ChatRepository chatRepository = mock(ChatRepository.class);
    protected final GptClient gptClient = mock(GptClient.class);
    protected final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

    protected final ChatService chatService = new ChatService(
            chatRepository, publisher
    );

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(any()))
                .willReturn(말랑.회원());
    }

    @AfterEach
    void tearDown() {
        reset(memberRepository, chatRepository, gptClient, publisher);
    }
}
