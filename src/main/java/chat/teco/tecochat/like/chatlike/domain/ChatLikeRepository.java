package chat.teco.tecochat.like.chatlike.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLikeRepository extends JpaRepository<ChatLike, Long> {

    boolean existsByMemberIdAndChatId(final Long memberId, final Long chatId);
}
