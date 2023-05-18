package chat.teco.tecochat.chat.application.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.reset;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.application.chat.service.ChatService;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.chat.GptClient;
import chat.teco.tecochat.chat.domain.chat.Question;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.chat.event.ChatCreatedEvent;
import chat.teco.tecochat.common.FakeTransactionTemplate;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("NonAsciiCharacters")
public class ChatCommandUseCaseTest {

    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final ChatRepository chatRepository = mock(ChatRepository.class);
    protected final GptClient gptClient = mock(GptClient.class);
    protected final TransactionTemplate transactionTemplate = FakeTransactionTemplate.spied();
    protected final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

    protected final ChatService chatService = new ChatService(
            memberRepository, chatRepository, gptClient, transactionTemplate, publisher
    );

    protected void GPT_의_응답을_지정한다(QuestionAndAnswer questionAndAnswer, QuestionAndAnswer... questionAndAnswers) {
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(questionAndAnswer, questionAndAnswers);
    }

    protected void 채팅_생성_이벤트_발행_확인() {
        then(publisher).should().publishEvent(any(ChatCreatedEvent.class));
    }

    @BeforeEach
    void setUp() {
        given(memberRepository.findById(any()))
                .willReturn(Optional.of(말랑.회원()));
    }

    @AfterEach
    void tearDown() {
        reset(memberRepository, chatRepository, gptClient, publisher);
    }
}
