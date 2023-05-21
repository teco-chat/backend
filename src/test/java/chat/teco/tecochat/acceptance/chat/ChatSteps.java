package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.common.AcceptanceTestSteps.given;
import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;
import static chat.teco.tecochat.chat.domain.chat.Answer.answer;
import static chat.teco.tecochat.chat.domain.chat.Question.question;
import static chat.teco.tecochat.chat.fixture.MockGptClient.KEYWORD;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.chat.domain.chat.QuestionAndAnswer;
import chat.teco.tecochat.chat.fixture.MockGptClient;
import chat.teco.tecochat.chat.presentation.chat.request.AskRequest;
import chat.teco.tecochat.chat.presentation.chat.request.CreateChatRequest;
import chat.teco.tecochat.chat.presentation.chat.request.UpdateChatTitleRequest;
import chat.teco.tecochat.chat.presentation.chat.response.CreateChatResponse;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.LikeCond;
import chat.teco.tecochat.chat.query.usecase.QueryChatByIdUseCase.QueryChatByIdResponse;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.common.presentation.PageResponse;
import chat.teco.tecochat.member.domain.Course;
import com.jayway.jsonpath.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("NonAsciiCharacters")
public class ChatSteps {

    public static void GPT_의_응답을_지정한다(
            MockGptClient gptClient,
            String 질문,
            String 답변,
            String... 질문_키워드들
    ) {
        if (질문_키워드들.length == 0) {
            gptClient.addQnA(질문, new QuestionAndAnswer(question(질문), answer(답변), 10));
            return;
        }
        String 키워드들 = String.join("||", 질문_키워드들);
        gptClient.addQnA(질문, new QuestionAndAnswer(question(질문), answer(답변), 10));
        gptClient.addQnA(KEYWORD, new QuestionAndAnswer(question(KEYWORD), answer(키워드들), 10));
    }

    public static ExtractableResponse<Response> 첫_채팅_요청(
            MockGptClient gptClient,
            String 크루명,
            String 질문,
            String 답변,
            String... 질문_키워드들
    ) {
        GPT_의_응답을_지정한다(gptClient, 질문, 답변, 질문_키워드들);
        return given(크루명)
                .body(toJson(new CreateChatRequest(질문)))
                .when()
                .post("/chats")
                .then()
                .log().all()
                .extract();
    }

    public static Long 첫_채팅_요청후_ID_반환(
            MockGptClient gptClient,
            String 크루명,
            String 질문,
            String 답변,
            String... 질문_키워드들
    ) {
        var 응답 = 첫_채팅_요청(gptClient, 크루명, 질문, 답변, 질문_키워드들);
        return 채팅_ID_반환(응답);
    }

    public static Long 채팅_ID_반환(ExtractableResponse<Response> 응답) {
        CreateChatResponse createChatResponse = 응답.as(CreateChatResponse.class);
        return createChatResponse.chatId();
    }

    public static void 첫_채팅의_응답을_확인한다(
            ExtractableResponse<Response> 응답,
            String 예상_답변
    ) {
        CreateChatResponse createChatResponse = 응답.as(CreateChatResponse.class);
        assertThat(createChatResponse.content()).isEqualTo(예상_답변);
    }

    public static ExtractableResponse<Response> 채팅_이어하기_요청(
            MockGptClient gptClient,
            String 이름,
            String 질문,
            String 답변,
            Long 채팅_ID
    ) {
        GPT_의_응답을_지정한다(gptClient, 질문, 답변);
        return given(이름)
                .body(toJson(new AskRequest(질문)))
                .when()
                .post("/chats/{id}", 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static void 채팅_이어하기의_응답을_확인한다(
            ExtractableResponse<Response> 응답,
            String 예상_답변
    ) {
        AskResponse askResponse = 응답.as(AskResponse.class);
        assertThat(askResponse.content()).isEqualTo(예상_답변);
    }


    public static ExtractableResponse<Response> 채팅_제목_수정_요청(
            Long 채팅_ID,
            String 이름,
            String 변경할_제목
    ) {
        return given(이름)
                .body(toJson(new UpdateChatTitleRequest(변경할_제목)))
                .when()
                .patch("/chats/{id}", 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 단일_채팅_조회_요청(Long 채팅_ID, String 이름) {
        return given(이름)
                .when()
                .get("/chats/{id}", 채팅_ID)
                .then()
                .log().all()
                .extract();
    }

    public static void 단일_채팅_조회_결과를_확인한다(
            ExtractableResponse<Response> 응답,
            QueryChatByIdResponse 예상_결과
    ) {
        QueryChatByIdResponse chatQueryResponse = 응답.as(QueryChatByIdResponse.class);
        assertThat(chatQueryResponse)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }

    public static ExtractableResponse<Response> 이름_과정_제목_좋아요_기간으로_검색_요청(
            Map<String, Object> 요청_파라미터들
    ) {
        return given()
                .when()
                .queryParams(요청_파라미터들)

                .get("/chats")
                .then().log().all()
                .extract();
    }

    public static Map<String, Object> 요청_파라미터들() {
        return new HashMap<>();
    }

    public static void 이름_조건(
            Map<String, Object> 요청_파라미터들,
            String 이름
    ) {
        요청_파라미터들.put("name", 이름);
    }

    public static void 과정_조건(
            Map<String, Object> 요청_파라미터들,
            Course 과정
    ) {
        요청_파라미터들.put("course", 과정);
    }

    public static void 제목_조건(
            Map<String, Object> 요청_파라미터들,
            String 제목
    ) {
        요청_파라미터들.put("title", 제목);
    }

    public static void 좋아요_기간_조겅(
            Map<String, Object> 요청_파라미터들,
            LikeCond 좋아요_기간
    ) {
        요청_파라미터들.put("likeCond", 좋아요_기간);
    }

    public static void 검색_결과의_내용_검증(
            ExtractableResponse<Response> 응답,
            List<SearchChatResponse> 예상_결과
    ) {
        PageResponse<SearchChatResponse> 페이지_결과 = 응답.as(
                new TypeRef<PageResponse<SearchChatResponse>>() {
                }.getType());
        List<SearchChatResponse> content = 페이지_결과.content();
        assertThat(content)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(예상_결과);
    }

    public static void 검색_결과_없음(
            ExtractableResponse<Response> 응답
    ) {
        PageResponse<SearchChatResponse> 페이지_결과 = 응답.as(
                new TypeRef<PageResponse<SearchChatResponse>>() {
                }.getType());
        assertThat(페이지_결과.totalElements()).isEqualTo(0);
    }
}
