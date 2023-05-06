package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.chat.exception.ChatExceptionType.NOT_FOUND_CHAT;
import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.exception.ChatException;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class WriteCommentService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final CommentRepository commentRepository;

    public WriteCommentService(final MemberRepository memberRepository,
                               final ChatRepository chatRepository,
                               final CommentRepository commentRepository) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.commentRepository = commentRepository;
    }

    public Long write(final WriteCommentCommand command) {
        validateMemberExist(command);
        validateChatExist(command);

        final Comment comment = command.toDomain();
        return commentRepository.save(comment).id();
    }

    private void validateMemberExist(final WriteCommentCommand command) {
        memberRepository.findById(command.memberId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    private void validateChatExist(final WriteCommentCommand command) {
        chatRepository.findById(command.chatId())
                .orElseThrow(() -> new ChatException(NOT_FOUND_CHAT));
    }

    public record WriteCommentCommand(
            Long chatId,
            Long memberId,
            String content
    ) {
        public Comment toDomain() {
            return new Comment(chatId, memberId, content);
        }
    }
}
