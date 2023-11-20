package chat.teco.tecochat.chat.domain.keyword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.keyword.Keyword;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Keyword 은(는)")
class KeywordTest {

    @Test
    void 복제할_수_있다() {
        // given
        Chat chat = 말랑_채팅.초기_채팅();
        Chat copiedChat = 허브_채팅.초기_채팅();
        Keyword keyword = new Keyword("말랑", chat);

        // when
        Keyword copied = keyword.copy(copiedChat);

        // then
        assertAll(
                () -> assertThat(copied.getKeyword()).isEqualTo("말랑"),
                () -> assertThat(copied.getChat()).isEqualTo(copiedChat)
        );
    }
}
