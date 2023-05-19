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

    private static final Question EXTRACT_KEYWORD_QUESTION = Question.question(
            "I need you to extract only 3 technical keywords from the previous 2 transcripts,"
                    + " excluding the \" and formatting them with || as a separator."
                    + " For example: ex1||ex2||ex3");

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GptClient gptClient;

    public KeywordExtractor(final GptClient gptClient) {
        this.gptClient = gptClient;
    }

    public List<Keyword> extractKeywords(final Chat chat) {
        final QuestionAndAnswer ask = gptClient.ask(chat, EXTRACT_KEYWORD_QUESTION);
        final List<Keyword> keywords = extractKeywords(chat, ask.answer());
        validateKeyword(keywords);
        return keywords;
    }

    private List<Keyword> extractKeywords(final Chat chat, final Answer answer) {
        return Arrays.stream(answer.content().split("\\|\\|"))
                .map(String::strip)
                .map(it -> new Keyword(it, chat))
                .toList();
    }

    private void validateKeyword(final List<Keyword> keywords) {
        if (keywords.size() != 3) {
            log.info("키워드가 3개가 반환되지 않음 [{}]", keywords);
            throw new KeywordException(CAN_NOT_EXTRACTED_KEYWORD);
        }
    }
}
