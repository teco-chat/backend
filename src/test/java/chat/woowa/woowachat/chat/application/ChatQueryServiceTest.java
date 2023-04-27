package chat.woowa.woowachat.chat.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatQueryRepository;
import chat.woowa.woowachat.chat.domain.ChatQueryRepository.ChatSearchCond;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.Message;
import chat.woowa.woowachat.chat.dto.ChatQueryDto;
import chat.woowa.woowachat.chat.dto.ChatQueryDto.MessageQueryDto;
import chat.woowa.woowachat.chat.dto.ChatSearchQueryDto;
import chat.woowa.woowachat.chat.fixture.ChatFixture;
import chat.woowa.woowachat.member.domain.Course;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
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

    @InjectMocks
    private ChatQueryService chatQueryService =
            new ChatQueryService(memberRepository, chatRepository, chatQueryRepository);

    @Test
    void 단일_채팅_기록을_전부_조회한다() {
        // given
        final Chat chat = ChatFixture.chatWithMessages(
                List.of(Message.user("안녕", 1),
                        Message.user("안녕하세요", 1),
                        Message.user("반가워 ", 1),
                        Message.user("저도 반갑습니다", 1)));
        given(chatRepository.findById(1L))
                .willReturn(Optional.of(chat));
        given(memberRepository.findById(any()))
                .willReturn(Optional.of(new Member("말랑", Course.BACKEND)));

        // when
        final ChatQueryDto chatQueryDto = chatQueryService.findById(1L);

        // then
        assertAll(
                () -> assertThat(chatQueryDto.crewName()).isEqualTo("말랑"),
                () -> assertThat(chatQueryDto.title()).isEqualTo("안녕"),
                () -> assertThat(chatQueryDto.messages())
                        .extracting(MessageQueryDto::content)
                        .containsExactly("안녕", "안녕하세요", "반가워 ", "저도 반갑습니다")
        );
    }

    @Test
    void 검색할_수_있다() {
        // given
        final Chat chat1 = ChatFixture.chatWithMessages(List.of(Message.user("제목1", 1)));
        final Chat chat2 = ChatFixture.chatWithMessages(List.of(Message.user("제목2", 1)));
        final Page<Chat> page = PageableExecutionUtils.getPage(
                List.of(chat1, chat2),
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
                () -> assertThat(chatSearchQueryDto.title()).isEqualTo("제목1")
        );
    }
}
