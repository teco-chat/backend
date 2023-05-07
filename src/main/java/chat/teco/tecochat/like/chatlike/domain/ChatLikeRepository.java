package chat.teco.tecochat.like.chatlike.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLikeRepository extends JpaRepository<ChatLike, Long> {

    Optional<ChatLike> findByMemberIdAndChatId(final Long memberId, final Long chatId);
}
