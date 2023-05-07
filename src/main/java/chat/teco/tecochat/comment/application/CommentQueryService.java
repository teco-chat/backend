package chat.teco.tecochat.comment.application;

import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import chat.teco.tecochat.member.exception.MemberException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class CommentQueryService {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public CommentQueryService(final MemberRepository memberRepository, final CommentRepository commentRepository) {
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
    }

    public List<CommentQueryDto> findAllByChatId(final Long chatId) {
        final List<Comment> comments = commentRepository.findAllByChatId(chatId);

        final List<Long> memberIds = comments.stream()
                .map(Comment::memberId)
                .toList();
        memberRepository.findAllById(memberIds);  // n + 1 문제 해결

        return comments.stream()
                .map(it -> CommentQueryDto.of(it, findMemberById(it.memberId())))
                .toList();
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }

    public record CommentQueryDto(
            Long id,
            String crewName,
            Course course,
            String content,
            LocalDateTime createdAt
    ) {
        public static CommentQueryDto of(final Comment comment, final Member member) {
            return new CommentQueryDto(
                    comment.id(),
                    member.name(),
                    member.course(),
                    comment.content(),
                    comment.createdAt()
            );
        }
    }
}
