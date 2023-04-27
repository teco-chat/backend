package chat.woowa.woowachat.chat.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.Message;
import chat.woowa.woowachat.chat.dto.ChatQueryDto;
import chat.woowa.woowachat.chat.dto.ChatQueryDto.MessageQueryDto;
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

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("ChatQueryService 은(는)")
class ChatQueryServiceTest {

    private final ChatRepository chatRepository = mock(ChatRepository.class);

    private final MemberRepository memberRepository = mock(MemberRepository.class);

    @InjectMocks
    private ChatQueryService chatQueryService = new ChatQueryService(memberRepository, chatRepository);

    @Test
    void 단일_채팅_기록을_전부_조회한다() {
        // given
        final Chat chat = ChatFixture.chatWithMessages(
                List.of(Message.user("안녕", 1), Message.user("안녕하세요", 1), Message.user("반가워 ", 1),
                        Message.user("저도 반갑습니다", 1)));
        given(chatRepository.findById(1L)).willReturn(Optional.of(chat));
        given(memberRepository.findById(any())).willReturn(Optional.of(new Member("말랑", Course.BACKEND)));

        // when
        final ChatQueryDto chatQueryDto = chatQueryService.findById(1L);

        // then
        assertThat(chatQueryDto.crewName()).isEqualTo("말랑");
        assertThat(chatQueryDto.title()).isEqualTo("안녕");
        assertThat(chatQueryDto.messages()).extracting(MessageQueryDto::content)
                .containsExactly("안녕", "안녕하세요", "반가워 ", "저도 반갑습니다");
    }
}
