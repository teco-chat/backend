package chat.teco.tecochat.comment.application.usecase;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.comment.application.CommentCommandUseCaseTest;
import chat.teco.tecochat.comment.application.usecase.WriteCommentUseCase.WriteCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.fixture.CommentFixture.말랑이_댓글;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("WriteCommentUseCase(댓글 작성) 은(는)")
class WriteCommentUseCaseTest extends CommentCommandUseCaseTest {

    private final WriteCommentUseCase writeCommentUseCase = commentService;

    @Test
    void 댓글을_작성한다() {
        // given
        회원을_저장한다(말랑.회원());
        채팅을_저장한다(말랑_채팅.초기_채팅());
        Comment 저장될_댓글 = 말랑이_댓글.댓글(말랑_채팅.ID);
        WriteCommentCommand command = 말랑이_댓글.댓글_생성_명령어(말랑_채팅.ID);
        given(commentRepository.save(any(Comment.class)))
                .willReturn(저장될_댓글);

        // when
        Long id = writeCommentUseCase.write(command);

        // then
        then(commentRepository).should(times(1)).save(any());
        assertThat(id).isEqualTo(저장될_댓글.id());
    }

    @Test
    void 회원이_없다면_오류() {
        // given
        회원이_저장되지_않았다(말랑.회원());
        WriteCommentCommand command = 말랑이_댓글.댓글_생성_명령어(말랑_채팅.ID);

        // when & then
        BaseExceptionType exceptionType = assertThrows(MemberException.class, () ->
                writeCommentUseCase.write(command)
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    void 채팅이_없다면_오류() {
        // given
        회원을_저장한다(말랑.회원());
        채팅이_저장되지_않았다(말랑_채팅.초기_채팅());
        WriteCommentCommand command = 말랑이_댓글.댓글_생성_명령어(말랑_채팅.ID);

        // when & then
        BaseExceptionType exceptionType = assertThrows(ChatException.class, () ->
                writeCommentUseCase.write(command)
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(NOT_FOUND_CHAT);
    }
}
