package chat.teco.tecochat.comment.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    void findAllByChatId(final Long chatId);
}
