package chat.teco.tecochat.chat.domain.chat;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByMemberId(Long memberId);

    @Query("select c from Chat c left join fetch c.questionAndAnswers.questionAndAnswers qnas where c.id = :id")
    Optional<Chat> findWithQuestionAndAnswersById(@Param("id") Long id);
}
