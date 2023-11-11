package chat.teco.tecochat.comment.query;

import static chat.teco.tecochat.member.exception.MemberExceptionType.NOT_FOUND_MEMBER;

import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.query.usecase.QueryAllCommentByChatIdUseCase;
import chat.teco.tecochat.domain.member.MemberRepository;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.exception.MemberException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CommentQueryService implements QueryAllCommentByChatIdUseCase {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<CommentQueryDto> findAllByChatId(Long chatId) {
        List<Comment> comments = commentRepository.findAllByChatId(chatId);
        memberRepository.findAllById(memberIds(comments));  // n + 1 문제 해결
        return mapToCommentQueryDtos(comments);
    }

    private List<Long> memberIds(List<Comment> comments) {
        return comments.stream()
                .map(Comment::memberId)
                .toList();
    }

    private List<CommentQueryDto> mapToCommentQueryDtos(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentQueryDto.of(comment, findMemberById(comment.memberId())))
                .toList();
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
