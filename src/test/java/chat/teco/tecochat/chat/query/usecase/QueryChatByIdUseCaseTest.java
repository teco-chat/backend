package chat.teco.tecochat.chat.query.usecase;

import static chat.teco.tecochat.member.fixture.MemberFixture.말랑;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import chat.teco.tecochat.chat.fixture.ChatFixture.말랑_채팅;
import chat.teco.tecochat.chat.query.ChatQueryService;
import chat.teco.tecochat.domain.chat.Chat;
import chat.teco.tecochat.query.QueryChatByIdResponse;
import chat.teco.tecochat.query.QueryMessageDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("QueryChatByIdUseCase(채팅 단일 조회) 은(는)")
class QueryChatByIdUseCaseTest extends ChatQueryUseCaseTest {

    private final ChatQueryService queryChatByIdUseCase = chatQueryService;

    @Test
    void 단일_채팅_기록을_전부_조회한다() {
        // given
        Chat chat = 말랑_채팅.초기_채팅();
        chat.addQuestionAndAnswer(말랑_채팅.QNA_1);
        chat.addQuestionAndAnswer(말랑_채팅.QNA_2);
        chat.addQuestionAndAnswer(말랑_채팅.QNA_3);
        회원을_저장한다(말랑.회원());
        채팅을_저장한다(chat);

        // when
        QueryChatByIdResponse response = queryChatByIdUseCase.findById(1L,
                말랑.ID);

        // then
        assertAll(
                () -> assertThat(response.getCrewName()).isEqualTo(말랑.이름),
                () -> assertThat(response.getTitle()).isEqualTo(chat.getTitle()),
                () -> assertThat(response.getMessages())
                        .extracting(QueryMessageDto::getContent)
                        .containsExactly(
                                말랑_채팅.QNA_1.getQuestion().content(),
                                말랑_채팅.QNA_1.getAnswer().content(),
                                말랑_채팅.QNA_2.getQuestion().content(),
                                말랑_채팅.QNA_2.getAnswer().content(),
                                말랑_채팅.QNA_3.getQuestion().content(),
                                말랑_채팅.QNA_3.getAnswer().content()
                        )
        );
    }
}
