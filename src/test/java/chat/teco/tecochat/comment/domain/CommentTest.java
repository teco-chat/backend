package chat.teco.tecochat.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import chat.teco.tecochat.comment.execption.CommentException;
import chat.teco.tecochat.comment.execption.CommentExceptionType;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Comment 은(는)")
class CommentTest {

    @Test
    void 작성자는_수정할_수_있다() {
        // given
        final Long 작성자_ID = 1L;
        final Long 질문_ID = 10L;
        final Comment 댓글 = new Comment(질문_ID, 작성자_ID, "댓글");

        // when
        댓글.update(작성자_ID, "변경");

        // then
        assertThat(댓글.content()).isEqualTo("변경");
    }

    @Test
    void 작성자가_아닌데_수정하려는_경우_예외() {
        // given
        final Long 작성자_ID = 1L;
        final Long 질문_ID = 10L;
        final Comment 댓글 = new Comment(질문_ID, 작성자_ID, "댓글");

        // when & then
        BaseExceptionType exceptionType = assertThrows(CommentException.class, () ->
                댓글.update(작성자_ID + 1L, "변경")
        ).exceptionType();

        assertThat(exceptionType).isEqualTo(CommentExceptionType.NO_AUTHORITY_UPDATE_COMMENT);
    }
}
