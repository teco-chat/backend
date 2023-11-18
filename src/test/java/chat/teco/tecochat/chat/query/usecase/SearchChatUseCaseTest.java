package chat.teco.tecochat.chat.query.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.chat.fixture.ChatFixture.허브_채팅;
import chat.teco.tecochat.chat.query.dao.ChatQueryDao.ChatSearchCond;
import chat.teco.tecochat.chat.query.usecase.SearchChatUseCase.SearchChatResponse;
import chat.teco.tecochat.domain.member.Course;
import chat.teco.tecochat.member.fixture.MemberFixture.허브;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("SearchChatUseCase(채팅 검색) 은(는)")
class SearchChatUseCaseTest extends ChatQueryUseCaseTest {

    private final SearchChatUseCase searchChatUseCase = chatQueryService;

    @Test
    void 검색할_수_있다() {
        // given
        Chat chat1 = 말랑_채팅.초기_채팅();
        chat1.addQuestionAndAnswer(말랑_채팅.QNA_1);
        chat1.addQuestionAndAnswer(말랑_채팅.QNA_2);
        chat1.addQuestionAndAnswer(말랑_채팅.QNA_3);

        Chat chat2 = 허브_채팅.초기_채팅();
        chat2.addQuestionAndAnswer(허브_채팅.QNA_1);
        chat2.addQuestionAndAnswer(허브_채팅.QNA_2);
        chat2.addQuestionAndAnswer(허브_채팅.QNA_3);
        회원을_저장한다(chat.teco.tecochat.member.fixture.MemberFixture.말랑.회원());
        회원을_저장한다(허브.회원());
        페이징_조회_결과_설정(chat1, chat2);

        // when
        Page<SearchChatResponse> search = searchChatUseCase.search(
                new ChatSearchCond(null, null, null, null),
                PageRequest.of(1, 1)
        );

        // then
        SearchChatResponse 말랑이_채팅_검색_결과 = search.getContent().get(0);
        SearchChatResponse 허브_채팅_검색_결과 = search.getContent().get(1);
        assertAll(
                () -> assertThat(말랑이_채팅_검색_결과.crewName()).isEqualTo("말랑"),
                () -> assertThat(말랑이_채팅_검색_결과.course()).isEqualTo(Course.BACKEND),
                () -> assertThat(말랑이_채팅_검색_결과.title()).isEqualTo(chat1.title()),

                () -> assertThat(허브_채팅_검색_결과.crewName()).isEqualTo("허브"),
                () -> assertThat(허브_채팅_검색_결과.course()).isEqualTo(Course.FRONTEND),
                () -> assertThat(허브_채팅_검색_결과.title()).isEqualTo(chat2.title())
        );
    }
}
