package chat.teco.tecochat.chat.application.chat.usecase;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.chat.domain.chat.SettingMessage.BACK_END_SETTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.application.chat.ChatCommandUseCaseTest;
import chat.teco.tecochat.chat.application.chat.usecase.CopyChatUseCase.CopyCommand;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.event.ChatCopiedEvent;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CopyChatUseCase(채팅 복사) 은(는)")
class CopyChatUseCaseTest extends ChatCommandUseCaseTest {

    private final CopyChatUseCase copyChatUseCase = chatService;

    @Test
    void 채팅을_복사한다() {
        // given
        Chat chat = 말랑_채팅.초기_채팅();
        chat.addQuestionAndAnswer(말랑_채팅.QNA_1);
        chat.addQuestionAndAnswer(말랑_채팅.QNA_2);
        given(chatRepository.findById(chat.id())).willReturn(Optional.of(chat));

        Chat copied = new Chat(2L, GPT_4, BACK_END_SETTING, "제목", 100L);
        copied.addQuestionAndAnswer(말랑_채팅.QNA_1);
        copied.addQuestionAndAnswer(말랑_채팅.QNA_2);
        given(chatRepository.save(any(Chat.class)))
                .willReturn(copied);

        // when
        Long copiedId = copyChatUseCase.copy(new CopyCommand(chat.id(), 100L));

        // then
        assertThat(copiedId).isEqualTo(2L);
    }

    @Test
    void 채팅_복사_후_채팅_복사_이벤트를_발행한다() {
        // given
        Chat chat = 말랑_채팅.초기_채팅();
        chat.addQuestionAndAnswer(말랑_채팅.QNA_1);
        chat.addQuestionAndAnswer(말랑_채팅.QNA_2);
        given(chatRepository.findById(chat.id())).willReturn(Optional.of(chat));

        Chat copied = new Chat(2L, GPT_4, BACK_END_SETTING, "제목", 100L);
        copied.addQuestionAndAnswer(말랑_채팅.QNA_1);
        copied.addQuestionAndAnswer(말랑_채팅.QNA_2);
        given(chatRepository.save(any(Chat.class)))
                .willReturn(copied);

        // when
        copyChatUseCase.copy(new CopyCommand(chat.id(), 100L));

        // then
        then(publisher)
                .should(times(1))
                .publishEvent(any(ChatCopiedEvent.class));
    }
}
