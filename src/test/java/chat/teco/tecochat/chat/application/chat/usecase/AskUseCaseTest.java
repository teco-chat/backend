package chat.teco.tecochat.chat.application.chat.usecase;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.domain.chat.SettingMessage.BACK_END_SETTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import chat.teco.tecochat.chat.application.chat.ChatCommandUseCaseTest;
import chat.teco.tecochat.chat.application.chat.usecase.AskUseCase.AskCommand;
import chat.teco.tecochat.chat.application.chat.usecase.AskUseCase.AskResult;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("AskUseCase(채팅 이어가기) 은(는)")
class AskUseCaseTest extends ChatCommandUseCaseTest {

    private final AskUseCase askUseCase = chatService;

    @Test
    void 이전_채팅_기록에_추가로_질문과_답변을_반환하며_저장한다() {
        // given
        Chat chat = 말랑_채팅.초기_채팅();
        given(chatRepository.findWithQuestionAndAnswersById(1L))
                .willReturn(Optional.of(chat));

        QuestionAndAnswer qna2 = 말랑_채팅.QNA_2;
        GPT_의_응답을_지정한다(qna2);

        // when
        AskResult result = askUseCase.ask(1L, 말랑_채팅.채팅_이어가기_명령어);

        // then
        assertThat(result.answer()).isEqualTo("답변2");
    }

    @Test
    void 누적_메세지의_총_토큰_합이_모델의_최대_토큰_수를_넘어도_저장은_계속_누적된다() {
        // given
        Chat chat = new Chat(GPT_3_5_TURBO, BACK_END_SETTING, "제목", 1L);
        QuestionAndAnswer qna1 = new QuestionAndAnswer("질문1", "답변1");
        QuestionAndAnswer qna2 = new QuestionAndAnswer("질문2", "답변2");
        QuestionAndAnswer qna3 = new QuestionAndAnswer("질문3", "답변3");
        QuestionAndAnswer qna4 = new QuestionAndAnswer("질문4", "답변4");
        GPT_의_응답을_지정한다(qna1, qna2, qna3, qna4);
        given(chatRepository.findWithQuestionAndAnswersById(1L))
                .willReturn(Optional.of(chat));

        // when
        askUseCase.ask(1L, new AskCommand(1L, "질문1"));
        askUseCase.ask(1L, new AskCommand(1L, "질문2"));
        askUseCase.ask(1L, new AskCommand(1L, "질문3"));
        askUseCase.ask(1L, new AskCommand(1L, "질문4"));

        // then
        assertThat(chat.questionAndAnswers())
                .extracting(qna -> qna.answer().content())
                .containsExactly("답변1", "답변2", "답변3", "답변4");
        assertThat(chat.questionAndAnswers())
                .extracting(qna -> qna.question().content())
                .containsExactly("질문1", "질문2", "질문3", "질문4");
    }
}
