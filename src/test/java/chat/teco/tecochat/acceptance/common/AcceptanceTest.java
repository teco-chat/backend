package chat.teco.tecochat.acceptance.common;

import chat.teco.tecochat.chat.domain.keyword.Keyword;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.ChatRepository;
import chat.teco.tecochat.domain.chat.KeywordRepository;
import chat.teco.tecochat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.domain.member.MemberRepository;
import io.restassured.RestAssured;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.support.TransactionTemplate;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatController 인수 테스트")
@Sql("/sql/h2ChatTruncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class AcceptanceTest {

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected ChatRepository chatRepository;

    @Autowired
    protected KeywordRepository keywordRepository;

    @Autowired
    protected TransactionTemplate transactionTemplate;

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    protected Long 채팅_생성(
            String 크루명,
            String 질문,
            String 답변,
            String... 키워드들
    ) {
        return transactionTemplate.execute(status -> {
            Chat chat = Chat.defaultChat(memberRepository.getByName(크루명), 질문);
            chat.addQuestionAndAnswer(new QuestionAndAnswer(질문, 답변));
            chatRepository.save(chat);
            List<Keyword> list = Arrays.stream(키워드들)
                    .map(it -> new Keyword(it, chat))
                    .toList();
            keywordRepository.saveAll(list);
            return chat.getId();
        });
    }

    protected void 채팅_이어하기(
            String 질문,
            String 답변,
            Long 채팅_ID
    ) {
        transactionTemplate.executeWithoutResult(status -> {
            Chat 채팅 = chatRepository.getWithQuestionAndAnswersById(채팅_ID);
            채팅.addQuestionAndAnswer(new QuestionAndAnswer(질문, 답변));
        });
    }
}
