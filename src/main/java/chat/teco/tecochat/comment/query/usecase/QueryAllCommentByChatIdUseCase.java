package chat.teco.tecochat.comment.query.usecase;

import chat.teco.tecochat.domain.comment.Comment;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.domain.member.Member;
import java.time.LocalDateTime;
import java.util.List;

public interface QueryAllCommentByChatIdUseCase {

    List<CommentQueryDto> findAllByChatId(Long chatId);

    record CommentQueryDto(
            Long id,
            String crewName,
            Course course,
            String content,
            LocalDateTime createdAt
    ) {
        public static CommentQueryDto of(Comment comment, Member member) {
            return new CommentQueryDto(
                    comment.id(),
                    member.getName(),
                    member.getCourse(),
                    comment.getContent(),
                    comment.createdAt()
            );
        }
    }
}
