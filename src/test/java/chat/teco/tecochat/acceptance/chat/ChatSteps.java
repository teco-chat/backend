package chat.teco.tecochat.acceptance.chat;

import static chat.teco.tecochat.acceptance.AcceptanceTestSteps.given;
import static chat.teco.tecochat.acceptance.util.JsonMapper.toJson;
import static chat.teco.tecochat.chat.domain.Answer.answer;
import static chat.teco.tecochat.chat.domain.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.GptClient;
import chat.teco.tecochat.chat.domain.Question;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.chat.dto.AskRequest;
import chat.teco.tecochat.chat.dto.ChatQueryDto;
import chat.teco.tecochat.chat.dto.ChatQueryDto.KeywordQueryDto;
import chat.teco.tecochat.chat.dto.ChatQueryDto.MessageQueryDto;
import chat.teco.tecochat.member.domain.Course;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;

@SuppressWarnings("NonAsciiCharacters")
public class ChatSteps {

    public static void GPT_의_응답을_지정한다(
            final GptClient gptClient,
            final String question,
            final String answer,
            final int token
    ) {
        final QuestionAndAnswer qna =
                new QuestionAndAnswer(question(question), answer(answer), token);
        given(gptClient.ask(any(Chat.class), any(Question.class)))
                .willReturn(qna);
    }

    public static ExtractableResponse<Response> 첫_채팅_요청(final String 크루명, final String 질문내용, final String... 질문_키워드들) {
        return given(크루명)
                .body(toJson(new AskRequest(질문내용)))
                .when()
                .post("/chats")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 채팅_이어하기_요청(
            final String name,
            final String question,
            final Long chatId
    ) {
        return given(name)
                .body(toJson(new AskRequest(question)))
                .when()
                .post("/chats/{id}", chatId)
                .then()
                .log().all()
                .extract();
    }

    public static void 질문에_대해_응답됨(
            final ExtractableResponse<Response> response,
            final String content
    ) {
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("content")).isEqualTo(content);
    }

    public static ExtractableResponse<Response> 단일_채팅_조회_요청(final Long id, final String name) {
        return given(name)
                .when()
                .get("/chats/{id}", id)
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 이름_과정_제목으로_검색_요청(
            final String name,
            final Course course,
            final String title
    ) {
        return given()
                .when()
                .get("/chats" + "?name=" + name + "&course=" + course.name() + "&title=" + title)
                .then()
                .extract();
    }

    public static void 채팅의_정보가_조회된다(
            final ExtractableResponse<Response> 응답,
            final Long id,
            final String crewName,
            final String course,
            final String title
    ) {
        final ChatQueryDto chatQueryDto = 응답.as(ChatQueryDto.class);
        assertThat(chatQueryDto.id()).isEqualTo(id);
        assertThat(chatQueryDto.crewName()).isEqualTo(crewName);
        assertThat(chatQueryDto.course().name()).isEqualTo(course);
        assertThat(chatQueryDto.title()).isEqualTo(title);
    }

    public static void 채팅의_키워드_검증(
            final ExtractableResponse<Response> 응답,
            final String... 키워드들
    ) {
        final ChatQueryDto chatQueryDto = 응답.as(ChatQueryDto.class);
        assertThat(chatQueryDto.keywords())
                .extracting(KeywordQueryDto::keyword)
                .containsExactly(키워드들);
    }

    public static void N번째_질문_혹은_답변_내용_검증(
            final ExtractableResponse<Response> 응답,
            final int index,
            final String content,
            final String role
    ) {
        final ChatQueryDto chatQueryDto = 응답.as(ChatQueryDto.class);
        final List<MessageQueryDto> messages = chatQueryDto.messages();
        final MessageQueryDto messageQueryDto = messages.get(index);
        assertThat(messageQueryDto.content()).isEqualTo(content);
        assertThat(messageQueryDto.role()).isEqualTo(role);
        assertThat(messageQueryDto.content()).isNotNull();
    }
}
