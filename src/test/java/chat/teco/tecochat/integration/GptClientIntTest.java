package chat.teco.tecochat.integration;

import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.Answer;
import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.GptClient;
import chat.teco.tecochat.chat.domain.Question;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.chat.fixture.ChatFixture;
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
@DisplayName("GptClient 통합테스트")
@SpringBootTest
class GptClientIntTest {

    @Autowired
    private GptClient client;

    @Test
    void Chat_Completion_API_에_질문을_보내고_답변을_받아온다() {
        // given
        final Chat chat =
                ChatFixture.chat(
                        new QuestionAndAnswer(
                                Question.question("안녕?"),
                                Answer.answer("네. 안녕하세요"),
                                100
                        )
                );

        // when
        final QuestionAndAnswer ask = client.ask(chat, Question.question("다음 단어를 똑같이 말해봐. 안녕"));

        // then
        assertThat(ask.answer().content())
                .isEqualTo("안녕");
    }
}