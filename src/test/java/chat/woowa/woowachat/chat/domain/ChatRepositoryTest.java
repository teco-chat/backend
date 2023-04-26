package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.GptModel.GPT_3_5_TURBO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.woowa.woowachat.chat.fixture.ChatFixture;
import chat.woowa.woowachat.common.annotation.JpaRepositoryTest;
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
        final Chat chat = ChatFixture.chatWithMessages("안녕");

        // when
        final Chat saved = chatRepository.save(chat);

        // then
        flushAndClear();
        final Chat find = chatRepository.findById(saved.id()).get();
        assertAll(
                () -> assertThat(find.model()).isEqualTo(GPT_3_5_TURBO),
                () -> assertThat(find.title()).isEqualTo("안녕"),
                () -> assertThat(find.messages()).hasSize(1)
        );
    }

    @Test
    void 채팅에_메세지를_추가할_수_있다() {
        // given
        final Chat chat = chatRepository.save(ChatFixture.chatWithMessages("안녕"));

        // when
        chat.addMessage(Message.user("응 안녕", 2));

        // then
        flushAndClear();
        final Chat chat1 = chatRepository.findById(chat.id()).get();
        assertThat(chat1.messages())
                .extracting(Message::content)
                .containsExactly("안녕", "응 안녕");
    }

    private void flushAndClear() {
        em.flush();
        em.clear();
    }
}
