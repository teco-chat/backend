package chat.woowa.woowachat.chat.application;

import static chat.woowa.woowachat.chat.domain.Answer.answer;
import static chat.woowa.woowachat.chat.domain.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.Question;
import chat.woowa.woowachat.chat.domain.QuestionAndAnswer;
import chat.woowa.woowachat.chat.dto.AskCommand;
import chat.woowa.woowachat.chat.dto.MessageDto;
import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("AskChatService 은(는)")
@Transactional
@SpringBootTest
class AskChatServiceTest {

    final long memberId = 1L;

    @MockBean
    private MemberRepository memberRepository;

    @MockBean
    private GptClient gptClient;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private AskChatService askChatService;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUp() {
        given(memberRepository.findById(memberId)).willReturn(Optional.of(new Member("말랑", Course.BACKEND)));
    }

    @Test
    void 첫_채팅인_경우_채팅을_생성하고_질문과_답변을_반환하며_저장한다() {
        // given
        final QuestionAndAnswer qna = new QuestionAndAnswer(
                question("질문"),
                answer("답변"),
                10
        );
        given(gptClient.ask(
                any(Chat.class),
                any(Question.class))
        ).willReturn(qna);

        // when
        final MessageDto answerResponse = askChatService.createAsk(new AskCommand(memberId, "첫 메세지"));

        // then
        assertThat(answerResponse.content()).isEqualTo("답변");
    }

    @Test
    void 첫_채팅이_아닌_경우_이전_채팅_기록에_추가로_질문과_답변을_반환하며_저장한다() {
        // given
        final QuestionAndAnswer qna1 = new QuestionAndAnswer(
                question("질문1"),
                answer("답변1"),
                10
        );
        final QuestionAndAnswer qna2 = new QuestionAndAnswer(
                question("질문2"),
                answer("답변2"),
                10
        );
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(qna1, qna2);
        final MessageDto messageDto =
                askChatService.createAsk(new AskCommand(memberId, "질문1"));

        // when
        final MessageDto answerResponse = askChatService.ask(messageDto.chatId(),
                new AskCommand(memberId, "질문2"));

        // then
        assertThat(answerResponse.content()).isEqualTo("답변2");
        final Chat chat = chatRepository.findAllByMemberId(memberId).get(0);
        assertThat(chat.questionAndAnswers())
                .extracting(qna -> qna.answer().content())
                .containsExactly("답변1", "답변2");
    }

    @Test
    void 누적_메세지의_총_토큰_합이_모델의_최대_토큰_수를_넘어도_저장은_계속_누적된다() {
        // given
        final QuestionAndAnswer qna1 = new QuestionAndAnswer(
                question("질문1"),
                answer("답변1"),
                2000
        );
        final QuestionAndAnswer qna2 = new QuestionAndAnswer(
                question("질문2"),
                answer("답변2"),
                2000
        );
        final QuestionAndAnswer qna3 = new QuestionAndAnswer(
                question("질문3"),
                answer("답변3"),
                2000
        );
        final QuestionAndAnswer qna4 = new QuestionAndAnswer(
                question("질문4"),
                answer("답변4"),
                2000
        );
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(qna1, qna2, qna3, qna4);
        final Long chatId = askChatService.createAsk(new AskCommand(memberId, "질문1"))
                .chatId();

        // when
        askChatService.ask(chatId, new AskCommand(memberId, "질문2"));
        askChatService.ask(chatId, new AskCommand(memberId, "질문3"));
        askChatService.ask(chatId, new AskCommand(memberId, "질문4"));
        flushAndClear();

        // then
        final Chat chat = chatRepository.findAllByMemberId(memberId).get(0);
        assertThat(chat.questionAndAnswers())
                .extracting(qna -> qna.answer().content())
                .containsExactly("답변1", "답변2", "답변3", "답변4");
        assertThat(chat.questionAndAnswers())
                .extracting(qna -> qna.question().content())
                .containsExactly("질문1", "질문2", "질문3", "질문4");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
