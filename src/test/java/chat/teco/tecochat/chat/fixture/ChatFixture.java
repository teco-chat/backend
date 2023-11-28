package chat.teco.tecochat.chat.fixture;

import static chat.teco.tecochat.domain.chat.GptModel.GPT_3_5_TURBO;
import static chat.teco.tecochat.domain.chat.Role.ASSISTANT;
import static chat.teco.tecochat.domain.chat.Role.USER;
import static chat.teco.tecochat.domain.chat.SettingMessage.BACK_END_SETTING;
import static chat.teco.tecochat.domain.chat.SettingMessage.FRONT_END_SETTING;

import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.chat.GptModel;
import chat.teco.tecochat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.member.fixture.MemberFixture;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import chat.teco.tecochat.query.ChatResponse;
import chat.teco.tecochat.query.QueryKeywordDto;
import chat.teco.tecochat.query.QueryMessageDto;
import chat.teco.tecochat.query.SearchChatResponse;
import chat.teco.tecochat.query.SearchKeywordDto;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class ChatFixture {

    public static ChatResponse 단일_채팅_조회의_예상_결과(
            Long 채팅_ID,
            String 채팅한_크루_이름,
            Course 과정,
            String 제목,
            int 좋아요_수,
            boolean 이미_좋아요_눌렀는가,
            List<QueryMessageDto> 대화_내용,
            List<QueryKeywordDto> 키워드들
    ) {
        return new ChatResponse(채팅_ID,
                채팅한_크루_이름,
                과정,
                제목,
                좋아요_수,
                이미_좋아요_눌렀는가,
                LocalDateTime.now(),
                대화_내용,
                키워드들);
    }

    public static List<QueryMessageDto> 대화_내용(String... 질문과_답변들) {
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(질문과_답변들));
        List<QueryMessageDto> result = new ArrayList<>();
        while (!deque.isEmpty()) {
            result.add(new QueryMessageDto(deque.pollFirst(), USER.getRoleName(), LocalDateTime.now()));
            result.add(new QueryMessageDto(deque.pollFirst(), ASSISTANT.getRoleName(), LocalDateTime.now()));
        }
        return result;
    }

    public static List<QueryKeywordDto> 단일_채팅_키워드(String... 키워드들) {
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(키워드들));
        List<QueryKeywordDto> result = new ArrayList<>();
        while (!deque.isEmpty()) {
            result.add(new QueryKeywordDto(deque.pollFirst()));
        }
        return result;
    }

    public static List<SearchChatResponse> 채팅_검색_결과들(
            SearchChatResponse... 채팅_검색의_예상_결과들
    ) {
        return List.of(채팅_검색의_예상_결과들);
    }

    public static SearchChatResponse 채팅_검색_결과(
            Long 채팅_ID,
            Long 채팅한_크루_Id,
            String 채팅한_크루_이름,
            Course 과정,
            String 제목,
            int 좋아요_수,
            int 댓글_수,
            int 전체_질문답변_수,
            List<SearchKeywordDto> 키워드들
    ) {
        return new SearchChatResponse(채팅_ID,
                채팅한_크루_Id,
                채팅한_크루_이름,
                과정,
                제목,
                좋아요_수,
                댓글_수,
                전체_질문답변_수,
                키워드들,
                LocalDateTime.now());
    }

    public static List<SearchKeywordDto> 검색시_조회될_채팅_키워드(String... 키워드들) {
        Deque<String> deque = new ArrayDeque<>(Arrays.asList(키워드들));
        List<SearchKeywordDto> result = new ArrayList<>();
        while (!deque.isEmpty()) {
            result.add(new SearchKeywordDto(deque.pollFirst()));
        }
        return result;
    }

    public static Chat defaultChat() {
        return new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                "제목",
                1L);
    }

    public static Chat defaultChat(Long memberId) {
        return new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                "제목",
                memberId);
    }

    public static Chat chat(QuestionAndAnswer... questionAndAnswers) {
        Chat chat = new Chat(GPT_3_5_TURBO,
                BACK_END_SETTING,
                questionAndAnswers[0].getQuestion().content(),
                1L);

        for (QuestionAndAnswer questionAndAnswer : questionAndAnswers) {
            chat.addQuestionAndAnswer(questionAndAnswer);
        }
        return chat;
    }

    public static Chat chat(List<QuestionAndAnswer> questionAndAnswers) {
        return chatWithModel(GPT_3_5_TURBO, questionAndAnswers);
    }

    public static Chat chatWithModel(GptModel gptModel,
                                     List<QuestionAndAnswer> questionAndAnswers) {
        Chat chat = new Chat(gptModel,
                BACK_END_SETTING,
                questionAndAnswers.get(0).getQuestion().content(),
                1L);

        for (QuestionAndAnswer questionAndAnswer : questionAndAnswers) {
            chat.addQuestionAndAnswer(questionAndAnswer);
        }
        return chat;
    }

    public static class 말랑_채팅 {
        public static final Long ID = 1L;
        public static final QuestionAndAnswer QNA_1 = new QuestionAndAnswer("질문1", "답변1");
        public static final QuestionAndAnswer QNA_2 = new QuestionAndAnswer("질문2", "답변2");
        public static final QuestionAndAnswer QNA_3 = new QuestionAndAnswer("질문3", "답변3");

        public static Chat 초기_채팅() {
            return new Chat(ID, GPT_3_5_TURBO, BACK_END_SETTING, "질문1", MemberFixture.말랑.ID);
        }
    }

    public static class 허브_채팅 {
        public static final Long ID = 2L;
        public static final QuestionAndAnswer QNA_1 = new QuestionAndAnswer("질문1", "답변1");
        public static final QuestionAndAnswer QNA_2 = new QuestionAndAnswer("질문2", "답변2");
        public static final QuestionAndAnswer QNA_3 = new QuestionAndAnswer("질문3", "답변3");

        public static Chat 초기_채팅() {
            return new Chat(ID, GPT_3_5_TURBO, FRONT_END_SETTING, "질문1", 허브.ID);
        }
    }
}
