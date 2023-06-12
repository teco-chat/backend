package chat.teco.tecochat.chat.application.chat;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.chat.domain.chat.SettingMessage.BACK_END_SETTING;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NO_AUTHORITY_CHANGE_TITLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.application.chat.dto.CopyCommand;
import chat.teco.tecochat.chat.application.chat.dto.UpdateChatTitleCommand;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.domain.chat.event.ChatCopiedEvent;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatService 은(는)")
class ChatServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final ApplicationEventPublisher publisher = mock(ApplicationEventPublisher.class);

    private final ChatService chatService = new ChatService(chatRepository, publisher);

    @BeforeEach
    void setUp() {
        given(memberRepository.getById(any()))
                .willReturn(말랑.회원());
    }

    @Nested
    class 채팅_복사_시 {

        @Test
        void 채팅을_복사한다() {
            // given
            Chat chat = 말랑_채팅.초기_채팅();
            chat.addQuestionAndAnswer(말랑_채팅.QNA_1);
            chat.addQuestionAndAnswer(말랑_채팅.QNA_2);
            given(chatRepository.getById(chat.id())).willReturn(chat);

            Chat copied = new Chat(2L, GPT_4, BACK_END_SETTING, "제목", 100L);
            copied.addQuestionAndAnswer(말랑_채팅.QNA_1);
            copied.addQuestionAndAnswer(말랑_채팅.QNA_2);
            given(chatRepository.save(any(Chat.class)))
                    .willReturn(copied);

            // when
            Long copiedId = chatService.copy(new CopyCommand(chat.id(), 100L));

            // then
            assertThat(copiedId).isEqualTo(2L);
        }

        @Test
        void 채팅_복사_후_채팅_복사_이벤트를_발행한다() {
            // given
            Chat chat = 말랑_채팅.초기_채팅();
            chat.addQuestionAndAnswer(말랑_채팅.QNA_1);
            chat.addQuestionAndAnswer(말랑_채팅.QNA_2);
            given(chatRepository.getById(chat.id())).willReturn(chat);

            Chat copied = new Chat(2L, GPT_4, BACK_END_SETTING, "제목", 100L);
            copied.addQuestionAndAnswer(말랑_채팅.QNA_1);
            copied.addQuestionAndAnswer(말랑_채팅.QNA_2);
            given(chatRepository.save(any(Chat.class)))
                    .willReturn(copied);

            // when
            chatService.copy(new CopyCommand(chat.id(), 100L));

            // then
            then(publisher)
                    .should(times(1))
                    .publishEvent(any(ChatCopiedEvent.class));
        }

        @Nested
        class 채팅_제목_수정_시 {

            private final Member member = 말랑.회원();
            private final Chat chat = 말랑_채팅.초기_채팅();

            @Test
            void 채팅의_제목을_수정한다() {
                // given
                given(chatRepository.getById(chat.id())).willReturn(chat);
                String 변경할_제목 = "변경제목";

                // when
                chatService.updateTitle(
                        new UpdateChatTitleCommand(member.id(), chat.id(), 변경할_제목)
                );

                // then
                assertThat(chat.title()).isEqualTo("변경제목");
            }

            @Test
            void 채팅을_진행한_사람이_아닌경우_수정할_수_없다() {
                // given
                given(chatRepository.getById(chat.id())).willReturn(chat);
                String 변경할_제목 = "변경제목";

                // when
                BaseExceptionType baseExceptionType = assertThrows(ChatException.class,
                        () -> chatService.updateTitle(
                                new UpdateChatTitleCommand(허브.ID, chat.id(), 변경할_제목)
                        )).exceptionType();

                // then
                assertThat(baseExceptionType).isEqualTo(NO_AUTHORITY_CHANGE_TITLE);
                assertThat(chat.title()).isNotEqualTo("변경제목");
            }

            @Test
            void 채팅의_없으면_오류() {
                // given
                given(chatRepository.getById(chat.id())).willThrow(new ChatException(NOT_FOUND_CHAT));
                String 변경할_제목 = "변경제목";

                // when
                BaseExceptionType baseExceptionType = assertThrows(ChatException.class,
                        () -> chatService.updateTitle(
                                new UpdateChatTitleCommand(member.id(), chat.id(), 변경할_제목)
                        )).exceptionType();

                // then
                assertThat(baseExceptionType).isEqualTo(NOT_FOUND_CHAT);
            }
        }
    }
}
