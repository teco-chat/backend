package chat.teco.tecochat.comment.query.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.comment.query.CommentQueryService;
import chat.teco.tecochat.comment.query.usecase.QueryAllCommentByChatIdUseCase.CommentQueryDto;
import chat.teco.tecochat.domain.member.MemberRepository;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("QueryAllCommentByChatIdUseCase(채팅 ID로 모든 댓글 조회) 은(는)")
@Transactional
@SpringBootTest
class QueryAllCommentByChatIdUseCaseTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentQueryService commentQueryService;

    @Test
    void 채팅에_달린_댓글을_전부_조회한다() {
        // given
        Member 말랑 = 회원가입("말랑_좋아요");
        Member 허브 = 회원가입("허브_좋아요");
        Member 박스터 = 회원가입("박스터");
        Chat chat = 채팅등록();
        댓글등록(chat, 말랑);
        댓글등록(chat, 허브);
        댓글등록(chat, 박스터);
        flushAndClear();

        // when
        List<CommentQueryDto> commentQueryDtos = commentQueryService.findAllByChatId(chat.id());

        // then
        assertThat(commentQueryDtos)
                .extracting(CommentQueryDto::crewName)
                .containsExactly("말랑_좋아요", "허브_좋아요", "박스터");
    }

    private Member 회원가입(String name) {
        return memberRepository.save(new Member(name, Course.BACKEND));
    }

    private Chat 채팅등록() {
        return chatRepository.save(ChatFixture.defaultChat());
    }

    private void 댓글등록(Chat chat, Member member) {
        commentRepository.save(new Comment(chat.id(), member.id(), "안녕"));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
        System.out.println("\n=======[CLEAR]=======\n");
    }
}
