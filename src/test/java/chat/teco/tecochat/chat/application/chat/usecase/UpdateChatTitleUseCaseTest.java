package chat.teco.tecochat.chat.application.chat.usecase;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NO_AUTHORITY_CHANGE_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import chat.teco.tecochat.chat.application.chat.ChatCommandUseCaseTest;
import chat.teco.tecochat.chat.application.chat.usecase.UpdateChatTitleUseCase.UpdateChatTitleCommand;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("UpdateChatTitleUseCase(채팅 제목 수정) 은(는)")
class UpdateChatTitleUseCaseTest extends ChatCommandUseCaseTest {

    private final UpdateChatTitleUseCase updateChatTitleUseCase = chatService;

    private final Member member = 말랑.회원();
    private final Chat chat = 말랑_채팅.초기_채팅();

    @Test
    void 채팅의_제목을_수정한다() {
        // given
        given(chatRepository.findById(chat.id())).willReturn(Optional.of(chat));
        String 변경할_제목 = "변경제목";

        // when
        updateChatTitleUseCase.updateTitle(
                new UpdateChatTitleCommand(member.id(), chat.id(), 변경할_제목)
        );

        // then
        assertThat(chat.title()).isEqualTo("변경제목");
    }

    @Test
    void 채팅을_진행한_사람이_아닌경우_수정할_수_없다() {
        // given
        given(chatRepository.findById(chat.id())).willReturn(Optional.of(chat));
        String 변경할_제목 = "변경제목";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ChatException.class,
                () -> updateChatTitleUseCase.updateTitle(
                        new UpdateChatTitleCommand(허브.ID, chat.id(), 변경할_제목)
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_TITLE);
        assertThat(chat.title()).isNotEqualTo("변경제목");
    }

    @Test
    void 채팅의_없으면_오류() {
        // given
        given(chatRepository.findById(chat.id())).willReturn(Optional.empty());
        String 변경할_제목 = "변경제목";

        // when
        BaseExceptionType baseExceptionType = assertThrows(ChatException.class,
                () -> updateChatTitleUseCase.updateTitle(
                        new UpdateChatTitleCommand(member.id(), chat.id(), 변경할_제목)
                )).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CHAT);
    }
}
