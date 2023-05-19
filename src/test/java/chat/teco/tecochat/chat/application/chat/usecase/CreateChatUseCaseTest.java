package chat.teco.tecochat.chat.application.chat.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.application.chat.ChatCommandUseCaseTest;
import chat.teco.tecochat.chat.application.chat.usecase.CreateChatUseCase.CreateChatResult;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.chat.event.ChatCreatedEvent;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CreateChatUseCaseTest(채팅 생성) 은(는)")
class CreateChatUseCaseTest extends ChatCommandUseCaseTest {

    private final CreateChatUseCase createChatUseCase = chatService;

    @Test
    void 채팅을_생성하고_질문과_답변을_반환하며_저장한다() {
        // given
        QuestionAndAnswer qna1 = 말랑_채팅.QNA_1;
        GPT_의_응답을_지정한다(qna1);

        // when
        CreateChatResult 첫_채팅_생성_결과 = createChatUseCase.createChat(말랑_채팅.채팅_생성_명령어);

        // then
        assertAll(
                this::채팅_생성_이벤트_발행_확인,
                () -> assertThat(첫_채팅_생성_결과.answer())
                        .isEqualTo(qna1.answer().content())
        );
    }

    @Test
    void 채팅_생성시_이벤트가_발행된다() {
        // given
        QuestionAndAnswer qna1 = 말랑_채팅.QNA_1;
        GPT_의_응답을_지정한다(qna1);

        // when
        createChatUseCase.createChat(말랑_채팅.채팅_생성_명령어);

        // then
        then(publisher)
                .should(times(1))
                .publishEvent(any(ChatCreatedEvent.class));
    }
}
