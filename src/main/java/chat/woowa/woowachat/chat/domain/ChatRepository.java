package chat.woowa.woowachat.chat.domain;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findAllByMemberId(final Long memberId);
}
