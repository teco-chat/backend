package chat.teco.tecochat.chat.domain.keyword;

import chat.teco.tecochat.domain.chat.Answer;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.Question;
import chat.teco.tecochat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.domain.keyword.GptClient;
import chat.teco.tecochat.domain.keyword.Keyword;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KeywordExtractor {

    public static final Question EXTRACT_KEYWORD_QUESTION = Question.Companion.question(
            "Except for this message, "
            + "I need you to extract 3 technical keywords "
            + "in CSV format without \". "
            + "Please use || as a separator. For example: keyword1||keyword2||keyword3");

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GptClient gptClient;

    public KeywordExtractor(GptClient gptClient) {
        this.gptClient = gptClient;
    }

    public List<Keyword> extractKeywords(Chat chat) {
        QuestionAndAnswer questionAndAnswer = chat.getQuestionAndAnswers().getQuestionAndAnswers().get(0);
        Answer answer = gptClient.ask(List.of(
                questionAndAnswer.getQuestion(),
                questionAndAnswer.getAnswer(),
                EXTRACT_KEYWORD_QUESTION));
        List<Keyword> keywords = extractKeywords(chat, answer);
        validateKeyword(keywords);
        return keywords;
    }

    private List<Keyword> extractKeywords(Chat chat, Answer answer) {
        return Arrays.stream(answer.content().split("\\|\\|"))
                .map(String::strip)
                .map(it -> new Keyword(it, chat))
                .toList();
    }

    private void validateKeyword(List<Keyword> keywords) {
        if (keywords.size() != 3) {
            log.info("키워드가 3개가 반환되지 않음 [{}]", keywords);
            throw new IllegalStateException("키워드 추출에 실패했습니다.");
        }
    }
}
