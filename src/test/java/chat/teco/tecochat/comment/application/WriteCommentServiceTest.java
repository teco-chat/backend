package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.comment.application.WriteCommentService.WriteCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("WriteCommentService 은(는)")
class WriteCommentServiceTest {

    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final CommentRepository commentRepository = mock(CommentRepository.class);

    private final WriteCommentService writeCommentService =
            new WriteCommentService(memberRepository, chatRepository, commentRepository);

    private final Long 댓글_ID = 1L;
    private final Long 작성자_ID = 2L;
    private final Long 채팅_ID = 10L;
    private final Member member = new Member("말랑", Course.BACKEND);
    private final Chat chat = ChatFixture.defaultChat();
    private final Comment comment = new Comment(채팅_ID, 작성자_ID, "안녕하세요");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(comment, "id", 댓글_ID);
    }

    @Test
    void 댓글을_작성한다() {
        // given
        given(memberRepository.findById(작성자_ID)).willReturn(Optional.of(member));
        given(chatRepository.findById(채팅_ID)).willReturn(Optional.of(chat));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);
        final WriteCommentCommand command = new WriteCommentCommand(채팅_ID, 작성자_ID, "안녕하세요");

        // when
        final Long id = writeCommentService.write(command);

        // then
        assertThat(id).isEqualTo(댓글_ID);
    }

    @Test
    void 회원이_없다면_오류() {
        // given
        given(memberRepository.findById(작성자_ID)).willReturn(Optional.empty());
        final WriteCommentCommand command = new WriteCommentCommand(채팅_ID, 작성자_ID, "안녕하세요");

        // when & then
        final BaseExceptionType exceptionType = assertThrows(MemberException.class, () ->
                writeCommentService.write(command)
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(NOT_FOUND_MEMBER);
    }

    @Test
    void 채팅이_없다면_오류() {
        // given
        given(memberRepository.findById(작성자_ID)).willReturn(Optional.of(member));
        given(chatRepository.findById(채팅_ID)).willReturn(Optional.empty());
        final WriteCommentCommand command =
                new WriteCommentCommand(채팅_ID, 작성자_ID, "안녕하세요");

        // when & then
        final BaseExceptionType exceptionType = assertThrows(ChatException.class, () ->
                writeCommentService.write(command)
        ).exceptionType();
        assertThat(exceptionType).isEqualTo(NOT_FOUND_CHAT);
    }
}
