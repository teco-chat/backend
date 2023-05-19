package chat.teco.tecochat.like.chatlike.query.usecase;

import chat.teco.tecochat.member.domain.Course;
import java.time.LocalDateTime;
import java.util.List;

public interface QueryAllChatLikeByChatIdUseCase {

    List<QueryChatLikeByChatIdResponse> findAllByChatId(Long chatId);

    /**
     * 채팅에 달린 좋아요를 조회할때 반환되는 정보
     */
    record QueryChatLikeByChatIdResponse(
            Long id,
            LocalDateTime createdAt,
            MemberInfo memberInfo
    ) {
        public record MemberInfo(
                Long id,
                String crewName,
                Course course
        ) {
        }
    }
}
