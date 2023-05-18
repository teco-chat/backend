package chat.teco.tecochat.comment.application.usecase;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_UPDATE_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.comment.application.CommentCommandUseCaseTest;
import chat.teco.tecochat.comment.application.usecase.UpdateCommentUseCase.UpdateCommentCommand;
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
@DisplayName("UpdateCommentUseCase(댓글 수정) 은(는)")
class UpdateCommentUseCaseTest extends CommentCommandUseCaseTest {

    private final UpdateCommentUseCase updateCommentUseCase = commentService;

    @Test
    void 댓글의_작성자는_댓글을_수정할_수_있다() {
        // given
        Comment 댓글 = 말랑이_댓글.댓글(허브_채팅.ID);
        댓글을_저장한다(댓글);
        UpdateCommentCommand command = 말랑이_댓글.댓글_수정_명령어();

        // when
        updateCommentUseCase.update(command);

        // then
        assertThat(댓글.content()).isEqualTo(말랑이_댓글.수정할_내용);
    }

    @Test
    void 댓글의_작성자가_아니면_오류() {
        // given
        Comment 댓글 = 말랑이_댓글.댓글(허브_채팅.ID);
        댓글을_저장한다(댓글);
        UpdateCommentCommand command = new UpdateCommentCommand(댓글.id(), 허브.ID, "수정");

        // when & then
        BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                updateCommentUseCase.update(command)
        ).exceptionType();
        assertAll(
                () -> assertThat(exceptionType).isEqualTo(NO_AUTHORITY_UPDATE_COMMENT),
                () -> assertThat(댓글.content()).isEqualTo(말랑이_댓글.내용)
        );
    }

    @Test
    void 댓글이_없으면_오류() {
        // given
        댓글이_저장되지_않았다(말랑이_댓글.댓글(허브_채팅.ID));
        UpdateCommentCommand command = 말랑이_댓글.댓글_수정_명령어();

        // when & then
        BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                updateCommentUseCase.update(command)
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(NOT_FOUND_COMMENT);
    }
}
