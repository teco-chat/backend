package chat.teco.tecochat.chat.domain.keyword;

import static chat.teco.tecochat.domain.chat.GptModel.GPT_4;
import static chat.teco.tecochat.domain.chat.SettingMessage.BACK_END_SETTING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.domain.chat.Answer;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.domain.keyword.Keyword;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("KeywordExtractor(키워드 추출기) 은(는)")
class KeywordExtractorTest {

    private final GptClient gptClient = mock(GptClient.class);
    private final KeywordExtractor extractor = new KeywordExtractor(gptClient);
    private final Chat chat =
            new Chat(GPT_4, BACK_END_SETTING, "제목", 1L);

    @BeforeEach
    void setUp() {
        chat.addQuestionAndAnswer(new QuestionAndAnswer("q", "a"));
    }

    @Test
    void 채팅_생성_이벤트를_받아_해당_채팅의_키워드를_추출하여_저장한다() {
        // given
        given(gptClient.ask(anyList())).willReturn(
                Answer.answer("답변1||답변2||답변3"));

        // when
        List<Keyword> keywordList = extractor.extractKeywords(chat);

        // then
        assertThat(keywordList)
                .extracting(Keyword::getKeyword)
                .containsExactly("답변1", "답변2", "답변3");
    }

    @ParameterizedTest(name = "키워드가 || 로 나누었을 때 3개가 나오지 않는다면 예외. ex={0}")
    @ValueSource(strings = {"답변1||답변2||답변3||답변4", "안녕하세요 말랑 !"})
    void 키워드가_3개가_나오지_않느다면_예외처리한다(String keywords) {
        // given
        given(gptClient.ask(anyList()))
                .willReturn(Answer.answer(keywords));

        // expect
        assertThrows(IllegalStateException.class, () ->
                extractor.extractKeywords(chat)
        );
    }
}
