package chat.teco.tecochat.chat.application;

import static chat.teco.tecochat.chat.domain.Answer.answer;
import static chat.teco.tecochat.chat.domain.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.domain.Answer;
import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.ChatQueryRepository;
import chat.teco.tecochat.chat.domain.ChatQueryRepository.ChatSearchCond;
import chat.teco.tecochat.chat.domain.ChatRepository;
import chat.teco.tecochat.chat.domain.Question;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.chat.domain.keyword.KeywordRepository;
import chat.teco.tecochat.chat.dto.ChatQueryDto;
import chat.teco.tecochat.chat.dto.ChatQueryDto.MessageQueryDto;
import chat.teco.tecochat.chat.dto.ChatSearchQueryDto;
import chat.teco.tecochat.chat.fixture.ChatFixture;
import chat.teco.tecochat.like.chatlike.domain.ChatLikeRepository;
import chat.teco.tecochat.member.domain.Course;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatQueryService 은(는)")
class ChatQueryServiceTest {

    private final ChatRepository chatRepository = mock(ChatRepository.class);
    private final MemberRepository memberRepository = mock(MemberRepository.class);
    private final ChatQueryRepository chatQueryRepository = mock(ChatQueryRepository.class);
    private final ChatLikeRepository chatLikeRepository = mock(ChatLikeRepository.class);
    private final KeywordRepository keywordRepository = mock(KeywordRepository.class);

    @InjectMocks
    private ChatQueryService chatQueryService = new ChatQueryService(
            memberRepository, chatRepository, chatQueryRepository, chatLikeRepository, keywordRepository);

    @Test
    void 단일_채팅_기록을_전부_조회한다() {
        // given
        final Chat chat = ChatFixture.chat(
                new QuestionAndAnswer(
                        question("안녕"),
                        answer("안녕하세요"),
                        2
                ),
                new QuestionAndAnswer(
                        Question.question("반가워"),
                        Answer.answer("저도 반갑습니다"),
                        2
                ));
        given(chatRepository.findById(1L))
                .willReturn(Optional.of(chat));
        given(memberRepository.findById(any()))
                .willReturn(Optional.of(new Member("말랑", Course.BACKEND)));

        // when
        final ChatQueryDto chatQueryDto = chatQueryService.findById(1L, 1L);

        // then
        assertAll(
                () -> assertThat(chatQueryDto.crewName()).isEqualTo("말랑"),
                () -> assertThat(chatQueryDto.title()).isEqualTo("안녕"),
                () -> assertThat(chatQueryDto.messages())
                        .extracting(MessageQueryDto::content)
                        .containsExactly("안녕", "안녕하세요", "반가워", "저도 반갑습니다")
        );
    }

    @Test
    void 검색할_수_있다() {
        // given
        final Chat chat1 = ChatFixture.chat(
                new QuestionAndAnswer(
                        question("안녕"),
                        answer("안녕하세요"),
                        2
                ));

        final Chat chat = ChatFixture.chat(
                new QuestionAndAnswer(
                        question("안녕2"),
                        answer("안녕하세요2"),
                        2
                ));
        final Page<Chat> page = PageableExecutionUtils.getPage(
                List.of(chat1, chat),
                PageRequest.of(0, 10),
                () -> 0);
        given(chatQueryRepository.search(any(), any()))
                .willReturn(page);
        given(memberRepository.findById(any()))
                .willReturn(Optional.of(new Member("말랑", Course.BACKEND)));

        // when
        final Page<ChatSearchQueryDto> search = chatQueryService.search(
                new ChatSearchCond(null, null, null),
                PageRequest.of(1, 1)
        );

        // then
        final ChatSearchQueryDto chatSearchQueryDto = search.getContent().get(0);
        assertAll(
                () -> assertThat(chatSearchQueryDto.crewName()).isEqualTo("말랑"),
                () -> assertThat(chatSearchQueryDto.course()).isEqualTo(Course.BACKEND),
                () -> assertThat(chatSearchQueryDto.title()).isEqualTo("안녕")
        );
    }
}
