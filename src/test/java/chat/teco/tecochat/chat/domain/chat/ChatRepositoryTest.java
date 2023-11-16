package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.domain.chat.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.common.annotation.JpaRepositoryTest;
import chat.teco.tecochat.domain.chat.ChatRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("ChatRepository 는")
@JpaRepositoryTest
class ChatRepositoryTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private EntityManager em;

    @Test
    void 채팅과_메세지를_저장한다() {
        // given
        Chat chat = ChatFixture.chat(
                new QuestionAndAnswer("안녕", "응 안녕")
        );

        // when
        Chat saved = chatRepository.save(chat);

        // then
        flushAndClear();
        Chat find = chatRepository.findById(saved.id()).get();
        assertAll(
                () -> assertThat(find.modelName()).isEqualTo(GPT_3_5_TURBO.modelName()),
                () -> assertThat(find.title()).isEqualTo("안녕"),
                () -> assertThat(find.questionAndAnswers()).hasSize(1)
        );
    }

    @Test
    void 채팅에_메세지를_추가할_수_있다() {
        // given
        Chat chat = ChatFixture.chat(new QuestionAndAnswer("안녕", "응 안녕"));
        Chat saved = chatRepository.save(chat);

        // when
        saved.addQuestionAndAnswer(
                new QuestionAndAnswer("안녕2", "응 안녕2")
        );

        // then
        flushAndClear();
        Chat chat1 = chatRepository.findById(chat.id()).get();
        assertThat(chat1.questionAndAnswers())
                .extracting(QuestionAndAnswer::question)
                .containsExactly(question("안녕"), question("안녕2"));
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
