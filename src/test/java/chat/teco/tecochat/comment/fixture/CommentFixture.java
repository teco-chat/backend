package chat.teco.tecochat.comment.fixture;

import chat.teco.tecochat.comment.application.dto.DeleteCommentCommand;
import chat.teco.tecochat.comment.application.dto.UpdateCommentCommand;
import chat.teco.tecochat.comment.application.dto.WriteCommentCommand;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.query.usecase.QueryAllCommentByChatIdUseCase.CommentQueryDto;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.fixture.MemberFixture;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class CommentFixture {

    public static List<CommentQueryDto> 댓글_검색의_예상_결과들(
            CommentQueryDto... 댓글_검색의_예상_결과들
    ) {
        return List.of(댓글_검색의_예상_결과들);
    }

    public static CommentQueryDto 댓글_검색의_예상_결과(
            Long 댓글_ID,
            String 작성한_크루명,
            Course 코스,
            String 내용
    ) {
        return new CommentQueryDto(댓글_ID,
                작성한_크루명,
                코스,
                내용,
                null);
    }

    public static class 말랑이_댓글 {

        public static final Long ID = 1L;
        public static final String 내용 = "안녕 난 말랑이야";
        public static final String 수정할_내용 = "안녕 난 말랑이에서 수정됨";

        public static WriteCommentCommand 댓글_생성_명령어(Long 채팅_ID) {
            return new WriteCommentCommand(채팅_ID, 말랑.ID, 내용);
        }

        public static UpdateCommentCommand 댓글_수정_명령어() {
            return new UpdateCommentCommand(ID, 말랑.ID, 수정할_내용);
        }

        public static DeleteCommentCommand 댓글_제거_명령어() {
            return new DeleteCommentCommand(ID, 말랑.ID);
        }

        public static Comment 댓글(Long 채팅_ID) {
            return new Comment(ID, 채팅_ID, 말랑.ID, 내용);
        }
    }

    public static class 허브_댓글 {

        public static final Long ID = 2L;
        public static final String 내용 = "안녕 난 허브야";
        public static final String 수정할_내용 = "안녕 난 허브에서 수정됨";

        public static WriteCommentCommand 댓글_생성_명령어(Long 채팅_ID) {
            return new WriteCommentCommand(채팅_ID, MemberFixture.허브.ID, 내용);
        }

        public static UpdateCommentCommand 댓글_수정_명령어() {
            return new UpdateCommentCommand(ID, MemberFixture.허브.ID, 수정할_내용);
        }

        public static DeleteCommentCommand 댓글_제거_명령어() {
            return new DeleteCommentCommand(ID, MemberFixture.허브.ID);
        }

        public static Comment 댓글(Long 채팅_ID) {
            return new Comment(ID, 채팅_ID, MemberFixture.허브.ID, 내용);
        }
    }
}
