package chat.teco.tecochat.like.chatlike.fixture;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.like.chatlike.application.usecase.PushChatLikeUseCase.PushChatLikeCommand;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikeByChatIdUseCase.QueryChatLikeByChatIdResponse.MemberInfo;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikeByMemberIdResponse;
import chat.teco.tecochat.like.chatlike.query.usecase.QueryAllChatLikedByMemberIdUseCase.QueryChatLikeByMemberIdResponse.ChatInfo;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.fixture.MemberFixture;
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
                null,
                null,
                new MemberInfo(null, 이름, 과정));
    }

    public static List<QueryChatLikeByMemberIdResponse> 내가_좋아요_누른_채팅_조회_예상_결과들(
            QueryChatLikeByMemberIdResponse... 결과들
    ) {
        return List.of(결과들);
    }

    public static QueryChatLikeByMemberIdResponse 내가_좋아요_누른_채팅_조회_예상_결과(
            Long 채팅_ID,
            String 채팅_작성한_크루명,
            Course 과정,
            String 제목,
            int 좋아요_수,
            int 전체_질문답변_수
    ) {
        return new QueryChatLikeByMemberIdResponse(
                null,
                null,
                new ChatInfo(
                        null,
                        채팅_ID,
                        채팅_작성한_크루명,
                        과정,
                        제목,
                        좋아요_수,
                        전체_질문답변_수,
                        null));
    }

    public static class 말랑_좋아요 {

        public static final Long ID = 1L;
        public static final Member 회원 = MemberFixture.말랑.회원();
        public static final Chat 채팅 = 말랑_채팅.초기_채팅();

        public static ChatLike 좋아요() {
            return new ChatLike(ID, 회원.id(), 채팅.id());
        }

        public static PushChatLikeCommand 좋아요_클릭_명령어() {
            return new PushChatLikeCommand(회원.id(), 채팅.id());
        }
    }

    public static class 허브_좋아요 {

        public static final Long ID = 1L;
        public static final Member 회원 = MemberFixture.허브.회원();
        public static final Chat 채팅 = 허브_채팅.초기_채팅();

        public static ChatLike 좋아요() {
            return new ChatLike(ID, 회원.id(), 채팅.id());
        }

        public static PushChatLikeCommand 좋아요_클릭_명령어() {
            return new PushChatLikeCommand(회원.id(), 채팅.id());
        }
    }
}
