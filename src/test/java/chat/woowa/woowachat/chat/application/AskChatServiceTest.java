package chat.woowa.woowachat.chat.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.Message;
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
        final Message answer = Message.assistant("답변입니다.", 10);
        given(gptClient.ask(any(Chat.class))).willReturn(answer);

        // when
        final MessageDto answerResponse = askChatService.createAsk(new AskCommand(memberId, "첫 메세지", 5));

        // then
        assertThat(answerResponse.content()).isEqualTo(answer.content());
    }

    @Test
    void 첫_채팅이_아닌_경우_이전_채팅_기록에_추가로_질문과_답변을_반환하며_저장한다() {
        // given
        final Message answer1 = Message.assistant("1 답변입니다.", 10);
        final Message answer2 = Message.assistant("2 답변입니다.", 10);
        given(gptClient.ask(any(Chat.class)))
                .willReturn(answer1, answer2);
        final MessageDto messageDto =
                askChatService.createAsk(new AskCommand(memberId, "첫 메세지", 5));

        // when
        final MessageDto answerResponse = askChatService.ask(messageDto.chatId(),
                new AskCommand(memberId, "두번째 메세지", 10));

        // then
        assertThat(answerResponse.content()).isEqualTo(answer2.content());
        final Chat chat = chatRepository.findAllByMemberId(memberId).get(0);
        assertThat(chat.messages()).extracting(Message::content)
                .containsExactly("첫 메세지", "1 답변입니다.", "두번째 메세지", "2 답변입니다.");
    }

    @Test
    void 누적_메세지의_총_토큰_합이_모델의_최대_토큰_수를_넘어도_저장은_계속_누적된다() {
        // given
        final Message answer1 = Message.assistant("1 답변입니다.", 2000);
        final Message answer2 = Message.assistant("2 답변입니다.", 2000);
        final Message answer3 = Message.assistant("3 답변입니다.", 2000);
        final Message answer4 = Message.assistant("4 답변입니다.", 2000);
        given(gptClient.ask(any(Chat.class))).willReturn(answer1, answer2, answer3, answer4);
        final Long chatId = askChatService.createAsk(new AskCommand(memberId, "1 질문", 2000))
                .chatId();

        // when
        askChatService.ask(chatId, new AskCommand(memberId, "2 질문", 2000));
        askChatService.ask(chatId, new AskCommand(memberId, "3 질문", 2000));
        askChatService.ask(chatId, new AskCommand(memberId, "4 질문", 2000));
        flushAndClear();

        // then
        final Chat chat = chatRepository.findAllByMemberId(memberId).get(0);
        assertThat(chat.messages()).extracting(Message::content)
                .containsExactly("1 질문", "1 답변입니다.", "2 질문", "2 답변입니다.", "3 질문", "3 답변입니다.", "4 질문", "4 답변입니다.");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
