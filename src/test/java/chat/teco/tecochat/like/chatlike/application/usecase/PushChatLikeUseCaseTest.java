package chat.teco.tecochat.like.chatlike.application.usecase;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.like.chatlike.application.service.ChatLikeService;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.like.chatlike.fixture.LikeFixture.말랑_좋아요;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("PushChatLikeUseCase(좋아요 클릭) 은(는)")
class PushChatLikeUseCaseTest {

    private final ChatLikeRepository chatLikeRepository = mock(ChatLikeRepository.class);
    private final ChatRepository chatRepository = mock(ChatRepository.class);

    private final PushChatLikeUseCase pushChatLikeUseCase = new ChatLikeService(
            chatLikeRepository, chatRepository
    );

    @Test
    void 좋아요를_누르면_좋아요는_저장되고_채팅의_좋아요_수가_1_증가한다() {
        // given
        Chat 채팅 = 말랑_채팅.초기_채팅();
        채팅을_저장한다(채팅);
        좋아요를_누르지_않았다(chat.teco.tecochat.member.fixture.MemberFixture.말랑.회원(), 채팅);
        PushChatLikeCommand 좋아요_클릭_명령어 = 말랑_좋아요.좋아요_클릭_명령어();

        // when
        pushChatLikeUseCase.pushLike(좋아요_클릭_명령어);

        // then
        assertThat(채팅.likeCount()).isEqualTo(1);
        then(chatLikeRepository)
                .should()
                .save(any(ChatLike.class));
    }

    @Test
    void 좋아요를_또다시_누르면_좋아요는_제거되고_채팅의_좋아요_수가_1_감소한다() {
        // given
        Chat 채팅 = 말랑_채팅.초기_채팅();
        채팅을_저장한다(채팅);
        좋아요를_누른다(말랑.회원(), 채팅);
        assertThat(채팅.likeCount()).isEqualTo(1);
        PushChatLikeCommand 좋아요_클릭_명령어 = 말랑_좋아요.좋아요_클릭_명령어();

        // when
        pushChatLikeUseCase.pushLike(좋아요_클릭_명령어);

        // then
        then(chatLikeRepository).should(times(1))
                .delete(any(ChatLike.class));
        assertThat(채팅.likeCount()).isEqualTo(0);
    }

    @Test
    void 채팅이_없는_경우_예외이다() {
        // given
        PushChatLikeCommand 좋아요_클릭_명령어 = 말랑_좋아요.좋아요_클릭_명령어();

        // when
        BaseExceptionType exceptionType = assertThrows(ChatException.class, () ->
                pushChatLikeUseCase.pushLike(좋아요_클릭_명령어)
        ).exceptionType();

        // then
        assertThat(exceptionType).isEqualTo(NOT_FOUND_CHAT);
    }

    private void 채팅을_저장한다(Chat chat) {
        given(chatRepository.findById(chat.id())).willReturn(Optional.of(chat));
    }

    private void 좋아요를_누르지_않았다(Member 회원, Chat 채팅) {
        given(chatLikeRepository.findByMemberIdAndChatId(회원.id(), 채팅.id()))
                .willReturn(Optional.empty());
    }

    private void 좋아요를_누른다(Member 회원, Chat 채팅) {
        ChatLike chatLike = new ChatLike(1L, 회원.id(), 채팅.id());
        given(chatLikeRepository.findByMemberIdAndChatId(회원.id(), 채팅.id()))
                .willReturn(Optional.of(chatLike));
        채팅.increaseLike();
    }
}
