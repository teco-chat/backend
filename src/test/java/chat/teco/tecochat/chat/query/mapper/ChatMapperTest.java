package chat.teco.tecochat.chat.query.mapper;

import static chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import static org.assertj.core.api.Assertions.assertThat;

import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.domain.keyword.Keyword;
import chat.teco.tecochat.domain.member.Member;
import chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import chat.teco.tecochat.query.QueryChatByIdResponse;
import chat.teco.tecochat.query.QueryKeywordDto;
import chat.teco.tecochat.query.QueryMessageDto;
import chat.teco.tecochat.query.SearchChatResponse;
import chat.teco.tecochat.query.SearchKeywordDto;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatMapper(채팅 매퍼) 은(는)")
class ChatMapperTest {

    @Test
    void 채팅_단일_조회_결과로_매핑한다() {
        // given
        Member 회원 = 말랑.회원();
        Chat 초기_채팅 = 말랑_채팅.초기_채팅();
        초기_채팅.addQuestionAndAnswer(말랑_채팅.QNA_1);
        초기_채팅.addQuestionAndAnswer(말랑_채팅.QNA_2);
        List<Keyword> 키워드들 = List.of("1", "2", "3").stream()
                .map(it -> new Keyword(it, 초기_채팅))
                .toList();
        초기_채팅.increaseLike();
        초기_채팅.increaseLike();

        // when
        QueryChatByIdResponse response =
                ChatMapper.mapToQueryResponse(초기_채팅, 회원, true, 키워드들);

        // then
        assertThat(response.getId()).isEqualTo(초기_채팅.getId());
        assertThat(response.getCourse()).isEqualTo(회원.getCourse());
        assertThat(response.getLikeCount()).isEqualTo(2);
        assertThat(response.getCrewName()).isEqualTo("말랑");
        assertThat(response.isAlreadyClickLike()).isTrue();
        assertThat(response.getMessages())
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(LocalDateTime.class)
                .isEqualTo(List.of(
                        new QueryMessageDto("질문1", "user", LocalDateTime.now()),
                        new QueryMessageDto("답변1", "assistant", LocalDateTime.now()),
                        new QueryMessageDto("질문2", "user", LocalDateTime.now()),
                        new QueryMessageDto("답변2", "assistant", LocalDateTime.now())
                ));
        assertThat(response.getKeywords())
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new QueryKeywordDto("1"),
                        new QueryKeywordDto("2"),
                        new QueryKeywordDto("3")
                ));
    }

    @Test
    void 채팅_검색_결과로_매핑한다() {
        // given
        Member 회원 = 말랑.회원();
        Chat 초기_채팅 = 말랑_채팅.초기_채팅();
        초기_채팅.addQuestionAndAnswer(말랑_채팅.QNA_1);
        초기_채팅.addQuestionAndAnswer(말랑_채팅.QNA_2);
        List<Keyword> 키워드들 = List.of("1", "2", "3").stream()
                .map(it -> new Keyword(it, 초기_채팅))
                .toList();
        초기_채팅.increaseLike();
        초기_채팅.increaseLike();

        // when
        SearchChatResponse response = ChatMapper.mapToSearchQueryResponse(초기_채팅, 회원, 키워드들);

        // then
        assertThat(response.getId()).isEqualTo(초기_채팅.getId());
        assertThat(response.getCrewId()).isEqualTo(회원.getId());
        assertThat(response.getCrewName()).isEqualTo("말랑");
        assertThat(response.getCourse()).isEqualTo(회원.getCourse());
        assertThat(response.getTitle()).isEqualTo(초기_채팅.getTitle());
        assertThat(response.getLikeCount()).isEqualTo(2);
        assertThat(response.getTotalQnaCount()).isEqualTo(2);
        assertThat(response.getKeywords())
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new SearchKeywordDto("1"),
                        new SearchKeywordDto("2"),
                        new SearchKeywordDto("3")
                ));
    }
}
