package chat.teco.tecochat.like.chatlike.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.like.chatlike.application.PushChatLikeService.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("PushChatLikeService 은(는)")
class PushChatLikeServiceTest {

    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final ChatLikeRepository chatLikeRepository = mock(ChatLikeRepository.class);

    private final PushChatLikeService pushChatLikeService = new PushChatLikeService(chatLikeRepository, chatRepository);

    private final Long memberId = 1L;
    private final Long chatId = 1L;
    private final Chat chat = ChatFixture.defaultChat();
    private final PushChatLikeCommand command = new PushChatLikeCommand(memberId, chatId);

    @Test
    void 좋아요를_누르면_좋아요는_저장되고_채팅의_좋아요_수가_1_증가한다() {
        // given
        given(chatRepository.findById(chatId)).willReturn(Optional.of(chat));
        given(chatLikeRepository.findByMemberIdAndChatId(memberId, chatId)).willReturn(Optional.empty());

        // when
        pushChatLikeService.pushLike(command);

        // then
        then(chatLikeRepository)
                .should()
                .save(any(ChatLike.class));
        assertThat(chat.likeCount()).isEqualTo(1);
    }

    @Test
    void 좋아요를_또다시_누르면_좋아요는_제거되고_채팅의_좋아요_수가_1_감소한다() {
        // given
        given(chatRepository.findById(chatId)).willReturn(Optional.of(chat));

        좋아요를_누른다();

        given(chatLikeRepository.findByMemberIdAndChatId(memberId, chatId))
                .willReturn(Optional.of(new ChatLike(memberId, chatId)));

        // when
        pushChatLikeService.pushLike(command);

        // then
        verify(chatLikeRepository, times(1)).delete(any(ChatLike.class));
        assertThat(chat.likeCount()).isEqualTo(0);
    }

    private void 좋아요를_누른다() {
        given(chatLikeRepository.findByMemberIdAndChatId(memberId, chatId))
                .willReturn(Optional.empty());
        pushChatLikeService.pushLike(command);
        assertThat(chat.likeCount()).isEqualTo(1);
    }

    @Test
    void 채팅이_없는_경우_예외이다() {
        // given
        given(chatRepository.findById(chatId)).willThrow(new ChatException(NOT_FOUND_CHAT));

        // when
        final BaseExceptionType exceptionType = assertThrows(ChatException.class, () ->
                pushChatLikeService.pushLike(command)
        ).exceptionType();

        // then
        verify(chatLikeRepository, times(0)).save(any(ChatLike.class));
        assertThat(chat.likeCount()).isZero();
        assertThat(exceptionType).isEqualTo(NOT_FOUND_CHAT);
    }
}
