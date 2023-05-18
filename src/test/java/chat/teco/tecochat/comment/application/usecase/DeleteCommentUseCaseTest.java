package chat.teco.tecochat.comment.application.usecase;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_DELETE_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.comment.application.CommentCommandUseCaseTest;
import chat.teco.tecochat.comment.application.usecase.DeleteCommentUseCase.DeleteCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.execption.CommentException;
import chat.teco.tecochat.comment.fixture.CommentFixture.말랑이_댓글;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("DeleteCommentUseCase(댓글 삭제) 은(는)")
class DeleteCommentUseCaseTest extends CommentCommandUseCaseTest {

    private final DeleteCommentUseCase deleteCommentUseCase = commentService;

    @Test
    void 댓글의_작성자는_댓글을_제거할_수_있다() {
        // given
        Comment 댓글 = 말랑이_댓글.댓글(허브_채팅.ID);
        댓글을_저장한다(댓글);
        DeleteCommentCommand command = 말랑이_댓글.댓글_제거_명령어();

        // when
        deleteCommentUseCase.delete(command);

        // then
        then(commentRepository)
                .should(times(1))
                .delete(댓글);
    }

    @Test
    void 자신이_작성한_댓글이_아니라면_예외() {
        // given
        Comment 댓글 = 말랑이_댓글.댓글(허브_채팅.ID);
        댓글을_저장한다(댓글);
        DeleteCommentCommand command = new DeleteCommentCommand(댓글.id(), 허브.ID);

        // when
        BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                deleteCommentUseCase.delete(command)
        ).exceptionType();

        // then
        assertThat(exceptionType).isEqualTo(NO_AUTHORITY_DELETE_COMMENT);
        then(commentRepository)
                .should(times(0))
                .delete(댓글);
    }

    @Test
    void 댓글이_없다면_예외() {
        // given
        댓글이_저장되지_않았다(말랑이_댓글.댓글(허브_채팅.ID));
        DeleteCommentCommand command = 말랑이_댓글.댓글_제거_명령어();

        // when
        BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                deleteCommentUseCase.delete(command)
        ).exceptionType();

        // then
        assertThat(exceptionType).isEqualTo(NOT_FOUND_COMMENT);
        then(commentRepository)
                .should(times(0))
                .delete(any());
    }
}
