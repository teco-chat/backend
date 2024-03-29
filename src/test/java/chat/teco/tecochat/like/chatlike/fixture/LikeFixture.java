package chat.teco.tecochat.like.chatlike.fixture;

import chat.teco.tecochat.application.ChatLikeRequest;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chatlike.ChatLike;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.domain.member.Member;
import chat.teco.tecochat.member.fixture.MemberFixture;
import chat.teco.tecochat.query.MemberInfo;
import chat.teco.tecochat.query.QueryChatLikeByChatIdResponse;
import chat.teco.tecochat.query.QueryChatLikedByMemberIdResponse;
import chat.teco.tecochat.query.QueryLikedChatKeywordDto;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class LikeFixture {

    public static List<QueryChatLikeByChatIdResponse> 채팅에_달린_좋아요_조회_예상_결과들(
            QueryChatLikeByChatIdResponse... 채팅에_달린_좋아요_조회_예상_결과들
    ) {
        return List.of(채팅에_달린_좋아요_조회_예상_결과들);
    }

    public static QueryChatLikeByChatIdResponse 채팅에_달린_좋아요_조회_예상_결과(
            String 이름,
            Course 과정
    ) {
        return new QueryChatLikeByChatIdResponse(
                0L,
                LocalDateTime.now(),
                new MemberInfo(0L, 이름, 과정));
    }

    public static List<QueryChatLikedByMemberIdResponse> 내가_좋아요_누른_채팅_조회_결과들(
            QueryChatLikedByMemberIdResponse... 결과들
    ) {
        return List.of(결과들);
    }

    public static QueryChatLikedByMemberIdResponse 내가_좋아요_누른_채팅_조회_결과(
            Long 채팅_ID,
            String 채팅한_크루_이름,
            Course 과정,
            String 제목,
            int 좋아요_수,
            int 댓글_수,
            int 전체_질문답변_수,
            List<QueryLikedChatKeywordDto> 키워드들
    ) {
        return new QueryChatLikedByMemberIdResponse(
                채팅_ID,
                0L,
                채팅한_크루_이름,
                과정,
                제목,
                좋아요_수,
                댓글_수,
                전체_질문답변_수,
                키워드들,
                LocalDateTime.now());
    }

    public static List<QueryLikedChatKeywordDto> 조회될_채팅_키워드(String... 키워드들) {
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(키워드들));
        List<QueryLikedChatKeywordDto> result = new ArrayList<>();
        while (!deque.isEmpty()) {
            result.add(new QueryLikedChatKeywordDto(deque.pollFirst()));
        }
        return result;
    }

    public static class 말랑_좋아요 {

        public static final Long ID = 1L;
        public static final Member 회원 = MemberFixture.말랑.회원();
        public static final Chat 채팅 = 말랑_채팅.초기_채팅();

        public static ChatLike 좋아요() {
            return new ChatLike(ID, 회원.getId(), 채팅.getId());
        }

        public static ChatLikeRequest 좋아요_클릭_명령어() {
            return new ChatLikeRequest(채팅.getId());
        }
    }

    public static class 허브_좋아요 {

        public static final Long ID = 1L;
        public static final Member 회원 = MemberFixture.허브.회원();
        public static final Chat 채팅 = 허브_채팅.초기_채팅();

        public static ChatLike 좋아요() {
            return new ChatLike(ID, 회원.getId(), 채팅.getId());
        }

        public static ChatLikeRequest 좋아요_클릭_명령어() {
            return new ChatLikeRequest(채팅.getId());
        }
    }
}
