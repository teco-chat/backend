package chat.teco.tecochat.like.chatlike.application;

import static chat.teco.tecochat.member.domain.Course.ANDROID;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.config.JpaConfig;
import chat.teco.tecochat.common.config.QueryDslConfig;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByChatIdQueryDto;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByChatIdQueryDto.MemberInfo;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByMemberIdQueryDto;
import chat.teco.tecochat.like.chatlike.application.ChatLikeQueryService.ChatLikeByMemberIdQueryDto.ChatInfo;
import chat.teco.tecochat.like.chatlike.domain.ChatLike;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatLikeQueryService 은(는)")
@Import({JpaConfig.class, QueryDslConfig.class, ChatLikeQueryService.class})
@DataJpaTest
class ChatLikeQueryServiceTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatLikeRepository chatLikeRepository;

    @Autowired
    private ChatLikeQueryService chatLikeQueryService;

    @Autowired
    private EntityManager em;

    private Member 말랑;
    private Member 허브;
    private Member 박스터;
    private Chat 말랑_채팅;
    private Chat 허브_채팅;
    private Chat 박스터_채팅;
    private ChatLike 말랑_말랑채팅_좋아요;
    private ChatLike 허브_말랑채팅_좋아요;
    private ChatLike 박스터_말랑채팅_좋아요;
    private ChatLike 말랑_허브채팅_좋아요;
    private ChatLike 허브_허브채팅_좋아요;
    private ChatLike 박스터_허브채팅_좋아요;
    private ChatLike 말랑_박스터채팅_좋아요;
    private ChatLike 허브_박스터채팅_좋아요;
    private ChatLike 박스터_박스터채팅_좋아요;


    @BeforeEach
    void setUp() {
        말랑 = memberRepository.save(new Member("말랑", BACKEND));
        허브 = memberRepository.save(new Member("허브", Course.FRONTEND));
        박스터 = memberRepository.save(new Member("박스터", Course.ANDROID));
        말랑_채팅 = chatRepository.save(ChatFixture.defaultChat(말랑.id()));
        허브_채팅 = chatRepository.save(ChatFixture.defaultChat(허브.id()));
        박스터_채팅 = chatRepository.save(ChatFixture.defaultChat(박스터.id()));

        말랑_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.id(), 말랑_채팅.id()));
        허브_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.id(), 말랑_채팅.id()));
        박스터_말랑채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.id(), 말랑_채팅.id()));
        말랑_채팅.increaseLike();
        말랑_채팅.increaseLike();
        말랑_채팅.increaseLike();

        말랑_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.id(), 허브_채팅.id()));
        허브_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.id(), 허브_채팅.id()));
        박스터_허브채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.id(), 허브_채팅.id()));
        허브_채팅.increaseLike();
        허브_채팅.increaseLike();
        허브_채팅.increaseLike();

        말랑_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(말랑.id(), 박스터_채팅.id()));
        허브_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(허브.id(), 박스터_채팅.id()));
        박스터_박스터채팅_좋아요 = chatLikeRepository.save(new ChatLike(박스터.id(), 박스터_채팅.id()));
        박스터_채팅.increaseLike();
        박스터_채팅.increaseLike();
        박스터_채팅.increaseLike();
        em.flush();
        em.clear();
    }

    @Test
    void 채팅에_달린_좋아요를_조회한다() {
        // when
        final List<ChatLikeByChatIdQueryDto> chatIdQueryDtos = chatLikeQueryService.findAllByChatId(말랑_채팅.id());

        // then
        assertThat(chatIdQueryDtos).hasSize(3);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(0),
                박스터_말랑채팅_좋아요.id(), 박스터.id(), "박스터", ANDROID);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(1),
                허브_말랑채팅_좋아요.id(), 허브.id(), "허브", FRONTEND);
        채팅으로_조회한_좋아요의_정보를_검증한다(chatIdQueryDtos.get(2),
                말랑_말랑채팅_좋아요.id(), 말랑.id(), "말랑", BACKEND);

    }

    private void 채팅으로_조회한_좋아요의_정보를_검증한다(
            final ChatLikeByChatIdQueryDto chatIdQueryDto,
            final Long chatLikeId,
            final Long memberId,
            final String memberName,
            final Course course
    ) {
        assertThat(chatIdQueryDto.id()).isEqualTo(chatLikeId);
        final MemberInfo memberInfo = chatIdQueryDto.memberInfo();
        assertThat(memberInfo.id()).isEqualTo(memberId);
        assertThat(memberInfo.crewName()).isEqualTo(memberName);
        assertThat(memberInfo.course()).isEqualTo(course);
    }

    @Test
    void 회원이_누른_좋아요를_조회한다() {
        // when
        final List<ChatLikeByMemberIdQueryDto> allByMemberId = chatLikeQueryService.findAllByMemberId(허브.id());

        // then
        회원으로_조회한_좋아요의_정보를_검증한다(allByMemberId.get(0),
                허브_박스터채팅_좋아요.id(),
                박스터_채팅.id(),
                박스터_채팅.memberId(),
                "박스터",
                ANDROID,
                박스터_채팅.title(),
                3,
                0);

        회원으로_조회한_좋아요의_정보를_검증한다(allByMemberId.get(1),
                허브_허브채팅_좋아요.id(),
                허브_채팅.id(),
                허브_채팅.memberId(),
                "허브",
                FRONTEND,
                허브_채팅.title(),
                3,
                0);

        회원으로_조회한_좋아요의_정보를_검증한다(allByMemberId.get(2),
                허브_말랑채팅_좋아요.id(),
                말랑_채팅.id(),
                말랑_채팅.memberId(),
                "말랑",
                BACKEND,
                말랑_채팅.title(),
                3,
                0);
    }

    private void 회원으로_조회한_좋아요의_정보를_검증한다(
            final ChatLikeByMemberIdQueryDto memberIdQueryDto,
            final Long chatLikeId,
            final Long chatId,
            final Long crewId,
            final String crewName,
            final Course course,
            final String title,
            final int likeCount,
            final int totalQnaCount
    ) {
        assertThat(memberIdQueryDto.id()).isEqualTo(chatLikeId);
        final ChatInfo chatInfo = memberIdQueryDto.chatInfo();
        assertThat(chatInfo.id()).isEqualTo(chatId);
        assertThat(chatInfo.crewId()).isEqualTo(crewId);
        assertThat(chatInfo.crewName()).isEqualTo(crewName);
        assertThat(chatInfo.course()).isEqualTo(course);
        assertThat(chatInfo.title()).isEqualTo(title);
        assertThat(chatInfo.likeCount()).isEqualTo(likeCount);
        assertThat(chatInfo.totalQnaCount()).isEqualTo(totalQnaCount);
    }
}
