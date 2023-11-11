package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_DELETE_COMMENT;
import static chat.teco.tecochat.comment.execption.CommentExceptionType.NO_AUTHORITY_UPDATE_COMMENT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.comment.application.dto.DeleteCommentCommand;
import chat.teco.tecochat.comment.application.dto.UpdateCommentCommand;
import chat.teco.tecochat.comment.application.dto.WriteCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.execption.CommentException;
import chat.teco.tecochat.comment.fixture.CommentFixture.말랑이_댓글;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.domain.member.MemberRepository;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.exception.MemberException;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("CommentService 은(는)")
class CommentServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);

    private final CommentService commentService = new CommentService(
            memberRepository, chatRepository, commentRepository
    );

    @Nested
    class 댓글_작성_시 {

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
            Long id = commentService.write(command);

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
                    commentService.write(command)
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
                    commentService.write(command)
            ).exceptionType();
            assertThat(exceptionType).isEqualTo(NOT_FOUND_CHAT);
        }
    }

    @Nested
    class 댓글_수정_시 {

        @Test
        void 댓글의_작성자는_댓글을_수정할_수_있다() {
            // given
            Comment 댓글 = 말랑이_댓글.댓글(허브_채팅.ID);
            댓글을_저장한다(댓글);
            UpdateCommentCommand command = 말랑이_댓글.댓글_수정_명령어();

            // when
            commentService.update(command);

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
                    commentService.update(command)
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
                    commentService.update(command)
            ).exceptionType();
            assertThat(exceptionType).isEqualTo(NOT_FOUND_COMMENT);
        }
    }

    @Nested
    class 댓글_삭제_시 {

        @Test
        void 댓글의_작성자는_댓글을_제거할_수_있다() {
            // given
            채팅을_저장한다(허브_채팅.초기_채팅());
            Comment 댓글 = 말랑이_댓글.댓글(허브_채팅.ID);
            댓글을_저장한다(댓글);
            DeleteCommentCommand command = 말랑이_댓글.댓글_제거_명령어();

            // when
            commentService.delete(command);

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
                    commentService.delete(command)
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
                    commentService.delete(command)
            ).exceptionType();

            // then
            assertThat(exceptionType).isEqualTo(NOT_FOUND_COMMENT);
            then(commentRepository)
                    .should(times(0))
                    .delete(any());
        }
    }

    private void 회원을_저장한다(Member 회원) {
        given(memberRepository.findById(회원.id())).willReturn(Optional.of(회원));
    }

    private void 회원이_저장되지_않았다(Member 회원) {
        given(memberRepository.findById(회원.id())).willReturn(Optional.empty());
    }

    private void 채팅을_저장한다(Chat 채팅) {
        given(chatRepository.getById(채팅.id())).willReturn(채팅);
    }

    private void 채팅이_저장되지_않았다(Chat 채팅) {
        given(chatRepository.getById(채팅.id())).willThrow(new ChatException(NOT_FOUND_CHAT));
    }

    private void 댓글을_저장한다(Comment 댓글) {
        given(commentRepository.getById(댓글.id())).willReturn(댓글);
    }

    private void 댓글이_저장되지_않았다(Comment 댓글) {
        given(commentRepository.getById(댓글.id())).willThrow(new CommentException(NOT_FOUND_COMMENT));
    }
}
