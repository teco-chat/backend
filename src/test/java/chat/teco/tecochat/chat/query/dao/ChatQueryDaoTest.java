package chat.teco.tecochat.chat.query.dao;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_3_5_TURBO;
import static java.time.DayOfWeek.MONDAY;
import static java.time.LocalTime.MIN;
import static java.time.temporal.TemporalAdjusters.previousOrSame;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.chat.SettingMessage;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.ChatSearchCond;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.LikeCond;
import chat.teco.tecochat.common.config.JpaConfig;
import chat.teco.tecochat.common.config.QueryDslConfig;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chatlike.ChatLike;
import chat.teco.tecochat.domain.chatlike.ChatLikeRepository;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.domain.member.Member;
import chat.teco.tecochat.domain.member.MemberRepository;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatQueryDao 은(는)")
@Import({QueryDslConfig.class, ChatQueryDao.class, JpaConfig.class})
@DataJpaTest
class ChatQueryDaoTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatQueryDao chatQueryDao;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private ChatLikeRepository chatLikeRepository;

    private Chat 채팅1;
    private Chat 채팅2;
    private Chat 채팅3;
    private Chat 채팅4;
    private Chat 채팅5;
    private Chat 채팅6;

    @BeforeEach
    void setUp() {
        채팅1 = saveMemberChat(memberRepository.save(new Member("프론트엔드허브", Course.FRONTEND, 0L)));
        채팅2 = saveMemberChat(memberRepository.save(new Member("프론트엔드말랑", Course.FRONTEND, 0L)));
        채팅3 = saveMemberChat(memberRepository.save(new Member("안드로이드허브", Course.ANDROID, 0L)));
        채팅4 = saveMemberChat(memberRepository.save(new Member("안드로이드말랑", Course.ANDROID, 0L)));
        채팅5 = saveMemberChat(memberRepository.save(new Member("백엔드허브", Course.BACKEND, 0L)));
        채팅6 = saveMemberChat(memberRepository.save(new Member("백엔드말랑", Course.BACKEND, 0L)));
        flushAndClear();
    }

    private Chat saveMemberChat(Member member) {
        Chat chat = new Chat(GPT_3_5_TURBO,
                SettingMessage.byCourse(member.getCourse()),
                member.getName() + "의 Title",
                member.id());

        QuestionAndAnswer qna = new QuestionAndAnswer(
                member.getName() + "의 Title",
                "안녕하세요");
        chat.addQuestionAndAnswer(qna);
        return chatRepository.save(chat);
    }

    @Test
    void 검색_조건이_설정되지_않으면_페이징하며_전체_조회() {
        // when
        Page<Chat> search = chatQueryDao.search(
                new ChatSearchCond(null, null, null, null),
                PageRequest.of(0, 20));

        // then
        List<Chat> content = search.getContent();
        assertAll(() -> assertThat(content).hasSize(6),
                () -> assertThat(content.get(0).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("백엔드말랑의 Title")
        );
    }

    @Test
    void 이름으로_검색_가능() {
        // when
        Page<Chat> search = chatQueryDao.search(new ChatSearchCond("말", null, null, null),
                PageRequest.of(0, 20));

        // then
        List<Chat> content = search.getContent();
        assertAll(() -> assertThat(content).hasSize(3),
                () -> assertThat(content.get(0).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("백엔드말랑의 Title"),
                () -> assertThat(content.get(1).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("안드로이드말랑의 Title"),
                () -> assertThat(content.get(2).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("프론트엔드말랑의 Title")
        );
    }

    @Test
    void 제목으로_검색_가능() {
        // when
        Page<Chat> search = chatQueryDao.search(new ChatSearchCond(null, " 엔드허브의 tiTlE    ", null, null),
                PageRequest.of(0, 20));

        // then
        List<Chat> content = search.getContent();
        assertAll(() -> assertThat(content).hasSize(2),
                () -> assertThat(content.get(0).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("백엔드허브의 Title"),
                () -> assertThat(content.get(1).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("프론트엔드허브의 Title")
        );
    }

    @Test
    void 과정으로_검색_가능() {
        // when
        Page<Chat> search = chatQueryDao.search(
                new ChatSearchCond(null, null, Course.ANDROID, null),
                PageRequest.of(0, 20));

        // then
        List<Chat> content = search.getContent();
        assertAll(() -> assertThat(content).hasSize(2),
                () -> assertThat(content.get(0).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("안드로이드말랑의 Title"),
                () -> assertThat(content.get(1).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("안드로이드허브의 Title")
        );
    }

    @Test
    void 이름_제목_과정으로_검색_가능() {
        // when
        Page<Chat> search = chatQueryDao.search(
                new ChatSearchCond("말랑_좋아요", "허브_좋아요", Course.BACKEND, null),
                PageRequest.of(0, 20));

        // then
        List<Chat> content = search.getContent();
        assertThat(content).hasSize(0);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }

    @Nested
    class 좋아요_순으로_정렬_시 {

        // 진짜 혹시나 하루가 넘어가는 시점에 실행하면 터질수도?
        private final LocalDateTime 오늘 = LocalDateTime.now().with(MIN);
        private final LocalDateTime 어제 = LocalDateTime.now().minusDays(1).with(MIN);
        private final LocalDateTime 이번주_시작 = LocalDateTime.now().with(previousOrSame(MONDAY)).with(MIN);
        private final LocalDateTime 저번주 = LocalDateTime.now().minusWeeks(1).with(MIN);
        private final LocalDateTime 이번달_시작 = LocalDateTime.now().withDayOfMonth(1).with(LocalTime.MIN);
        private final LocalDateTime 저번달 = LocalDateTime.now().minusMonths(1).with(MIN);
        private final LocalDateTime 올해_시작 = LocalDateTime.now().withDayOfYear(1).with(LocalTime.MIN);
        private final LocalDateTime 작년 = LocalDateTime.now().minusYears(1).with(MIN);

        @BeforeEach
        void setUp() {
            채팅1 = chatRepository.findById(채팅1.id()).get();
            채팅2 = chatRepository.findById(채팅2.id()).get();
            채팅3 = chatRepository.findById(채팅3.id()).get();
            채팅4 = chatRepository.findById(채팅4.id()).get();
            채팅5 = chatRepository.findById(채팅5.id()).get();
            채팅6 = chatRepository.findById(채팅6.id()).get();
        }

        @Test
        void 오늘_가장_좋아요를_많이_받은_게시물_순으로_조회한다() {
            // given
            좋아요(1L, 채팅4, 오늘);
            좋아요(2L, 채팅4, 오늘);
            좋아요(3L, 채팅4, 오늘);
            좋아요(4L, 채팅2, 오늘);
            좋아요(5L, 채팅2, 오늘);
            좋아요(5L, 채팅3, 오늘);

            // 아래 데이터는 어제 저장되었으므로 제외
            좋아요(1L, 채팅1, 어제);
            좋아요(2L, 채팅1, 이번달_시작);
            좋아요(3L, 채팅1, 올해_시작);

            // when
            Page<Chat> search = chatQueryDao.search(
                    new ChatSearchCond(null, null, null, LikeCond.TODAY),
                    PageRequest.of(0, 20));

            // then
            List<Chat> expected = List.of(
                    채팅4,
                    채팅2,
                    채팅3
            );
            assertThat(search.getContent())
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 이번주에_가장_많은_좋아요를_많이_받은_게시물_순으로_조회한다() {
            // given
            좋아요(1L, 채팅3, 오늘);
            좋아요(3L, 채팅3, 어제);
            좋아요(2L, 채팅3, 이번주_시작);
            좋아요(3L, 채팅6, 어제);
            좋아요(1L, 채팅6, 이번주_시작);
            좋아요(1L, 채팅5, 이번주_시작);

            // 아래 데이터는 저번주에 저장되었으므로 제외
            좋아요(1L, 채팅1, 저번주);
            좋아요(2L, 채팅1, 저번주);
            좋아요(3L, 채팅1, 올해_시작);

            // when
            Page<Chat> search = chatQueryDao.search(
                    new ChatSearchCond(null, null, null, LikeCond.WEEK),
                    PageRequest.of(0, 20));

            // then
            List<Chat> expected = List.of(
                    채팅3,
                    채팅6,
                    채팅5
            );
            assertThat(search.getContent())
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 이번달에_가장_많은_좋아요를_받은_게시물_순으로_조회한다() {
            // given
            좋아요(1L, 채팅3, 오늘);
            좋아요(3L, 채팅3, 어제);
            좋아요(2L, 채팅3, 이번달_시작);
            좋아요(3L, 채팅6, 이번달_시작);
            좋아요(1L, 채팅6, 이번달_시작);

            // 아래 데이터는 저번주에 저장되었으므로 제외
            좋아요(1L, 채팅1, 저번달);
            좋아요(2L, 채팅1, 저번달);

            // when
            Page<Chat> search = chatQueryDao.search(
                    new ChatSearchCond(null, null, null, LikeCond.MONTH),
                    PageRequest.of(0, 20));

            // then
            List<Chat> expected = List.of(
                    채팅3,
                    채팅6
            );
            assertThat(search.getContent())
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 올해_가장_많은_좋아요를_받은_게시물_순으로_조회한다() {
            // given
            좋아요(1L, 채팅3, 오늘);
            좋아요(3L, 채팅3, 어제);
            좋아요(2L, 채팅3, 이번달_시작);
            좋아요(3L, 채팅6, 올해_시작);
            좋아요(1L, 채팅6, 올해_시작);
            좋아요(1L, 채팅5, 저번달);
            좋아요(4L, 채팅2, 이번주_시작);

            // 아래 데이터는 저번주에 저장되었으므로 제외
            좋아요(1L, 채팅1, 작년);
            좋아요(2L, 채팅1, 작년);
            좋아요(3L, 채팅1, 작년);

            // when
            Page<Chat> search = chatQueryDao.search(
                    new ChatSearchCond(null, null, null, LikeCond.YEAR),
                    PageRequest.of(0, 20));

            // then
            List<Chat> expected = List.of(
                    채팅3,
                    채팅6,
                    채팅5,
                    채팅2
            );
            assertThat(search.getContent())
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        @Test
        void 전체_기간동안_가장_많은_좋아요를_받은_게시물을_조회한다() {
            // given
            좋아요(1L, 채팅3, 오늘);
            좋아요(3L, 채팅3, 어제);
            좋아요(2L, 채팅3, 이번달_시작);
            좋아요(3L, 채팅6, 올해_시작);
            좋아요(1L, 채팅6, 올해_시작);
            좋아요(1L, 채팅5, 저번달);
            좋아요(4L, 채팅2, 이번주_시작);
            좋아요(1L, 채팅1, 작년);
            좋아요(2L, 채팅1, 작년);
            좋아요(3L, 채팅1, 작년);

            // when
            Page<Chat> search = chatQueryDao.search(
                    new ChatSearchCond(null, null, null, LikeCond.ALL),
                    PageRequest.of(0, 20));

            // then
            List<Chat> expected = List.of(
                    채팅3,
                    채팅1,
                    채팅6,
                    채팅5,
                    채팅2,
                    채팅4  // 전체 기간의 경우 좋아요가 없더라도 보여줌
            );
            assertThat(search.getContent())
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
        }

        private void 좋아요(long 회원_ID, Chat 채팅, LocalDateTime 생성시간) {
            채팅.increaseLike();
            ChatLike chatLike = new ChatLike(회원_ID, 채팅.id(), 0L);
            ChatLike save = chatLikeRepository.save(chatLike);
            ReflectionTestUtils.setField(save, "createdAt", 생성시간);
            em.flush();
        }
    }
}
