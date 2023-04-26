package chat.woowa.woowachat.chat.domain;

import static org.assertj.core.api.Assertions.assertThat;

import chat.woowa.woowachat.chat.fixture.ChatFixture;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("GptClient 은(는)")
@SpringBootTest
class GptClientTest {

    @Autowired
    private GptClient client;

    @Test
    void Chat_Completion_API_에_질문을_보내고_답변을_받아온다() {
        // given
        final Chat chat = ChatFixture.chatWithMessages("안녕?", "네. 안녕하세요.", "다음 단어를 똑같이 말해봐. 안녕");

        // when
        final Message ask = client.ask(chat);

        // then
        assertThat(ask.content()).isEqualTo("안녕");
    }
}
