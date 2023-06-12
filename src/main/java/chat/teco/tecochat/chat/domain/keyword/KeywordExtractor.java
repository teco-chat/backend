package chat.teco.tecochat.chat.domain.keyword;

import static chat.teco.tecochat.chat.exception.keyword.KeywordExceptionType.CAN_NOT_EXTRACTED_KEYWORD;

import chat.teco.tecochat.chat.domain.chat.Answer;
import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.GptClient;
import chat.teco.tecochat.chat.domain.chat.Question;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.exception.keyword.KeywordException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KeywordExtractor {

    public static final Question EXTRACT_KEYWORD_QUESTION = Question.question(
            "Except for this message and the first one, "
                    + "I need you to extract 3 technical keywords from the above 2 conversations "
                    + "in CSV format without \". "
                    + "Please use || as a separator. For example: keyword1||keyword2||keyword3");

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GptClient gptClient;

    public KeywordExtractor(GptClient gptClient) {
        this.gptClient = gptClient;
    }

    public List<Keyword> extractKeywords(Chat chat) {
        QuestionAndAnswer ask = gptClient.ask(chat, EXTRACT_KEYWORD_QUESTION);
        List<Keyword> keywords = extractKeywords(chat, ask.answer());
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
            throw new KeywordException(CAN_NOT_EXTRACTED_KEYWORD);
        }
    }
}
