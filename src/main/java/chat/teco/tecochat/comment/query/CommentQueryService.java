package chat.teco.tecochat.comment.query;

import chat.teco.tecochat.comment.query.usecase.QueryAllCommentByChatIdUseCase;
import chat.teco.tecochat.domain.comment.Comment;
import chat.teco.tecochat.domain.comment.CommentRepository;
import chat.teco.tecochat.domain.member.MemberRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class CommentQueryService implements QueryAllCommentByChatIdUseCase {

    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public CommentQueryService(MemberRepository memberRepository, CommentRepository commentRepository) {
        this.memberRepository = memberRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public List<CommentQueryDto> findAllByChatId(Long chatId) {
        List<Comment> comments = commentRepository.findAllByChatId(chatId);
        memberRepository.findAllById(memberIds(comments));  // n + 1 문제 해결
        return mapToCommentQueryDtos(comments);
    }

    private List<Long> memberIds(List<Comment> comments) {
        return comments.stream()
                .map(Comment::getMemberId)
                .toList();
    }

    private List<CommentQueryDto> mapToCommentQueryDtos(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentQueryDto.of(comment, memberRepository.getById(comment.getMemberId())))
                .toList();
    }
}
