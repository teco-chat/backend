package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_DELETE_COMMENT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.comment.application.DeleteCommentService.DeleteCommentCommand;
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
@DisplayName("DeleteCommentService 은(는)")
class DeleteCommentServiceTest {

    private final CommentRepository commentRepository = mock(CommentRepository.class);
    private final DeleteCommentService deleteCommentService = new DeleteCommentService(commentRepository);

    private final Long 작성자_ID = 2L;
    private final Long 채팅_ID = 10L;
    private final Long 댓글_ID = 10L;
    private final Comment comment = new Comment(채팅_ID, 작성자_ID, "변경 전");

    @Test
    void 댓글의_작성자는_댓글을_제거할_수_있다() {
        // given
        given(commentRepository.findById(댓글_ID)).willReturn(Optional.of(comment));
        final DeleteCommentCommand command = new DeleteCommentCommand(댓글_ID, 작성자_ID);

        // when
        deleteCommentService.delete(command);

        // then
        then(commentRepository)
                .should(times(1))
                .delete(comment);
    }

    @Test
    void 자신이_작성한_댓글이_아니라면_예외() {
        // given
        given(commentRepository.findById(댓글_ID)).willReturn(Optional.of(comment));
        final DeleteCommentCommand command = new DeleteCommentCommand(댓글_ID, 작성자_ID + 1);

        // when
        final BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                deleteCommentService.delete(command)
        ).exceptionType();

        // then
        assertThat(exceptionType).isEqualTo(NO_AUTHORITY_DELETE_COMMENT);
        then(commentRepository)
                .should(times(0))
                .delete(comment);
    }

    @Test
    void 댓글이_없다면_예외() {
        // given
        given(commentRepository.findById(댓글_ID)).willReturn(Optional.empty());
        final DeleteCommentCommand command = new DeleteCommentCommand(댓글_ID, 작성자_ID + 1);

        // when
        final BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                deleteCommentService.delete(command)
        ).exceptionType();

        // then
        assertThat(exceptionType).isEqualTo(NOT_FOUND_COMMENT);
        then(commentRepository)
                .should(times(0))
                .delete(comment);
    }
}
