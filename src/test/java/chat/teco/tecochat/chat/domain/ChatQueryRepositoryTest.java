package chat.teco.tecochat.chat.domain;

import static chat.teco.tecochat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.member.domain.Course.ANDROID;
import static chat.teco.tecochat.member.domain.Course.BACKEND;
import static chat.teco.tecochat.member.domain.Course.FRONTEND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.domain.ChatQueryRepository.ChatSearchCond;
import chat.teco.tecochat.common.config.JpaConfig;
import chat.teco.tecochat.common.config.QueryDslConfig;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatQueryRepository 은(는)")
@Import({QueryDslConfig.class, ChatQueryRepository.class, JpaConfig.class})
@DataJpaTest
class ChatQueryRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ChatQueryRepository chatQueryRepository;

    @Autowired
    private ChatRepository chatRepository;


    @BeforeEach
    void setUp() {
        saveMemberChat(memberRepository.save(new Member("프론트엔드허브", FRONTEND)));
        saveMemberChat(memberRepository.save(new Member("프론트엔드말랑", FRONTEND)));
        saveMemberChat(memberRepository.save(new Member("안드로이드허브", ANDROID)));
        saveMemberChat(memberRepository.save(new Member("안드로이드말랑", ANDROID)));
        saveMemberChat(memberRepository.save(new Member("백엔드허브", BACKEND)));
        saveMemberChat(memberRepository.save(new Member("백엔드말랑", BACKEND)));
        flushAndClear();
    }

    private void saveMemberChat(final Member member) {
        final Chat chat = new Chat(GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course()),
                member.name() + "의 Title",
                member.id());

        QuestionAndAnswer qna = new QuestionAndAnswer(
                Question.question(member.name() + "의 Title"),
                Answer.answer("안녕하세요"),
                7
        );
        chat.addQuestionAndAnswer(qna);
        chatRepository.save(chat);
    }

    @Test
    void 검색_조건이_설정되지_않으면_페이징하며_전체_조회() {
        // when
        final Page<Chat> search = chatQueryRepository.search(
                new ChatSearchCond(null, null, null),
                PageRequest.of(0, 20));

        // then
        final List<Chat> content = search.getContent();
        assertAll(() -> assertThat(content).hasSize(6),
                () -> assertThat(content.get(0).questionAndAnswers())
                        .extracting(it -> it.question().content())
                        .containsExactly("백엔드말랑의 Title")
        );
    }

    @Test
    void 이름으로_검색_가능() {
        // when
        final Page<Chat> search = chatQueryRepository.search(new ChatSearchCond("말", null, null),
                PageRequest.of(0, 20));

        // then
        final List<Chat> content = search.getContent();
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
        final Page<Chat> search = chatQueryRepository.search(new ChatSearchCond(null, " 엔드허브의 tiTlE    ", null),
                PageRequest.of(0, 20));

        // then
        final List<Chat> content = search.getContent();
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
        final Page<Chat> search = chatQueryRepository.search(new ChatSearchCond(null, null, ANDROID),
                PageRequest.of(0, 20));

        // then
        final List<Chat> content = search.getContent();
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
        final Page<Chat> search = chatQueryRepository.search(new ChatSearchCond("말랑", "허브", BACKEND),
                PageRequest.of(0, 20));

        // then
        final List<Chat> content = search.getContent();
        assertThat(content).hasSize(0);
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
