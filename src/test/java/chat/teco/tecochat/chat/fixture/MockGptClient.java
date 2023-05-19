package chat.teco.tecochat.chat.fixture;

import static chat.teco.tecochat.chat.domain.keyword.KeywordExtractor.EXTRACT_KEYWORD_QUESTION;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.GptClient;
import chat.teco.tecochat.chat.domain.chat.Question;
import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Primary
@Component
public class MockGptClient extends GptClient {

    public static final String KEYWORD = EXTRACT_KEYWORD_QUESTION.content();

    private final Map<String, QuestionAndAnswer> questionAndQnaMapping = new ConcurrentHashMap<>();

    public MockGptClient() {
        super(null, null, null);
    }

    public void addQnA(String question, QuestionAndAnswer questionAndAnswer) {
        questionAndQnaMapping.put(question, questionAndAnswer);
    }

    @Override
    public QuestionAndAnswer ask(Chat chat, Question question) {
        return questionAndQnaMapping.get(question.content());
    }

    // AfterEach로 실행해주기
    public void clear() {
        questionAndQnaMapping.clear();
    }
}
