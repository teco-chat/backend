package chat.teco.tecochat.comment.domain;

import static chat.teco.tecochat.comment.execption.CommentExceptionType.NOT_FOUND_COMMENT;

import chat.teco.tecochat.comment.execption.CommentException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByChatId(Long chatId);

    default Comment getById(Long id) {
        return findById(id)
                .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));
    }
}
