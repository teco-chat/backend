package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_UPDATE_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.comment.application.UpdateCommentService.UpdateCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.execption.CommentException;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("UpdateCommentService 은(는)")
class UpdateCommentServiceTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final UpdateCommentService updateCommentService = new UpdateCommentService(commentRepository);

    private final Long 작성자_ID = 2L;
    private final Long 채팅_ID = 10L;
    private final Long 댓글_ID = 10L;
    private final Comment comment = new Comment(채팅_ID, 작성자_ID, "변경 전");

    @Test
    void 댓글의_작성자는_댓글을_수정할_수_있다() {
        // given
        given(commentRepository.findById(댓글_ID)).willReturn(Optional.of(comment));
        final UpdateCommentCommand command =
                new UpdateCommentCommand(댓글_ID, 작성자_ID, "변경한 내용");

        // when
        updateCommentService.update(command);

        // then
        assertThat(comment.content()).isEqualTo("변경한 내용");
    }

    @Test
    void 댓글의_작성자가_아니면_오류() {
        // given
        given(commentRepository.findById(댓글_ID)).willReturn(Optional.of(comment));

        final UpdateCommentCommand command =
                new UpdateCommentCommand(댓글_ID, 작성자_ID + 1, "변경한 내용");

        // when & then
        final BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                updateCommentService.update(command)
        ).exceptionType();
        assertAll(
                () -> assertThat(exceptionType).isEqualTo(NO_AUTHORITY_UPDATE_COMMENT),
                () -> assertThat(comment.content()).isEqualTo("변경 전")
        );
    }

    @Test
    void 댓글이_없으면_오류() {
        // given
        given(commentRepository.findById(댓글_ID)).willReturn(Optional.empty());
        final UpdateCommentCommand command =
                new UpdateCommentCommand(댓글_ID, 작성자_ID, "변경한 내용");

        // when & then
        final BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                updateCommentService.update(command)
        ).exceptionType();
        assertAll(
                () -> assertThat(exceptionType).isEqualTo(NOT_FOUND_COMMENT),
                () -> assertThat(comment.content()).isEqualTo("변경 전")
        );
    }
}
