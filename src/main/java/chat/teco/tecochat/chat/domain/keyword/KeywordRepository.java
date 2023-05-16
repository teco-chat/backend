package chat.teco.tecochat.chat.domain.keyword;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    List<Keyword> findAllByChatId(final Long chatId);

    @Query("select k from Keyword k where k.chat.id in (:chatIds)")
    List<Keyword> findAllInChatIds(@Param("chatIds") final List<Long> chatIds);
}
